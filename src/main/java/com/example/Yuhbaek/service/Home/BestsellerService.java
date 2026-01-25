package com.example.Yuhbaek.service.Home;

import com.example.Yuhbaek.config.Home.AladinApiConfig;
import com.example.Yuhbaek.dto.Home.BestsellerRequest;
import com.example.Yuhbaek.dto.Home.BestsellerResponse;
import com.example.Yuhbaek.entity.Home.BestsellerCache;
import com.example.Yuhbaek.repository.Home.BestsellerCacheRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BestsellerService {

    private final WebClient webClient;
    private final AladinApiConfig aladinApiConfig;
    private final BestsellerCacheRepository cacheRepository;
    private final ObjectMapper objectMapper;

    /**
     * 월간 베스트셀러 조회 (DB 캐싱)
     *
     * 동작 순서:
     * 1. DB에서 먼저 조회 (24시간 이내 데이터)
     * 2. DB에 없거나 오래됐으면 API 호출
     * 3. API 결과를 DB에 저장
     */
    @Transactional
    public BestsellerResponse getMonthlyBestsellers(BestsellerRequest request) {
        String cacheKey = generateCacheKey(request);

        // 1. DB에서 먼저 조회
        Optional<BestsellerCache> cached = cacheRepository.findByCacheKey(cacheKey);

        if (cached.isPresent() && cached.get().isRecent()) {
            log.info("✅ DB 캐시 히트 - 저장된 데이터 사용: {}", cacheKey);
            log.info("   마지막 업데이트: {}", cached.get().getUpdatedAt());
            return deserializeData(cached.get().getData());
        }

        // 2. DB에 없거나 오래됐으면 API 호출
        log.info("🔍 DB 캐시 미스 - 알라딘 API 호출: {}", cacheKey);
        BestsellerResponse response = fetchFromAPI(request);

        // 3. DB에 저장 (업데이트 or 생성)
        saveToCache(cacheKey, response);

        return response;
    }

    /**
     * 캐시 키 생성
     * 예: "Book_Bestseller_10"
     */
    private String generateCacheKey(BestsellerRequest request) {
        return String.format("%s_%s_%d",
                request.getSearchTarget(),
                request.getQueryType(),
                request.getMaxResults()
        );
    }

    /**
     * 알라딘 API 호출
     */
    private BestsellerResponse fetchFromAPI(BestsellerRequest request) {
        try {
            log.info("알라딘 API 호출 시작 - QueryType: {}, Target: {}",
                    request.getQueryType(), request.getSearchTarget());

            // 알라딘 API 호출
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("www.aladin.co.kr")
                            .path("/ttb/api/ItemList.aspx")
                            .queryParam("ttbkey", aladinApiConfig.getApiKey())
                            .queryParam("QueryType", request.getQueryType())
                            .queryParam("MaxResults", request.getMaxResults())
                            .queryParam("start", request.getStart())
                            .queryParam("SearchTarget", request.getSearchTarget())
                            .queryParam("output", "js")  // JSON 형식으로 응답
                            .queryParam("Version", "20131101")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("알라딘 API 응답 수신 완료");

            // JSON 파싱
            return parseResponse(response);

        } catch (Exception e) {
            log.error("베스트셀러 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("베스트셀러 조회 중 오류가 발생했습니다", e);
        }
    }

    /**
     * DB에 저장
     */
    private void saveToCache(String cacheKey, BestsellerResponse response) {
        try {
            String jsonData = objectMapper.writeValueAsString(response);

            Optional<BestsellerCache> existing = cacheRepository.findByCacheKey(cacheKey);

            if (existing.isPresent()) {
                // 업데이트
                BestsellerCache cache = existing.get();
                cache.setData(jsonData);
                cacheRepository.save(cache);
                log.info("💾 DB 캐시 업데이트: {}", cacheKey);
            } else {
                // 새로 생성
                BestsellerCache cache = BestsellerCache.builder()
                        .cacheKey(cacheKey)
                        .data(jsonData)
                        .build();
                cacheRepository.save(cache);
                log.info("💾 DB 캐시 생성: {}", cacheKey);
            }
        } catch (Exception e) {
            log.error("DB 캐시 저장 실패: {}", e.getMessage(), e);
            // 저장 실패해도 응답은 정상 반환
        }
    }

    /**
     * DB에서 가져온 JSON을 객체로 변환
     */
    private BestsellerResponse deserializeData(String jsonData) {
        try {
            return objectMapper.readValue(jsonData, BestsellerResponse.class);
        } catch (Exception e) {
            log.error("JSON 역직렬화 실패: {}", e.getMessage(), e);
            throw new RuntimeException("데이터 변환 중 오류가 발생했습니다", e);
        }
    }

    /**
     * 알라딘 API 응답 파싱
     */
    private BestsellerResponse parseResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            // 메타 정보 추출
            String title = root.path("title").asText();
            int totalResults = root.path("totalResults").asInt();
            int startIndex = root.path("startIndex").asInt();
            int itemsPerPage = root.path("itemsPerPage").asInt();
            String pubDateStr = root.path("pubDate").asText();

            // 날짜 파싱 (예: "Thu, 23 Jan 2025 12:00:00 GMT")
            LocalDateTime pubDate = null;
            if (!pubDateStr.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
                    pubDate = LocalDateTime.parse(pubDateStr, formatter);
                } catch (Exception e) {
                    log.warn("날짜 파싱 실패: {}", pubDateStr);
                }
            }

            // 아이템 목록 파싱
            List<BestsellerResponse.BestsellerItem> items = new ArrayList<>();
            JsonNode itemsNode = root.path("item");

            if (itemsNode.isArray()) {
                int rank = 1;
                for (JsonNode itemNode : itemsNode) {
                    BestsellerResponse.BestsellerItem item = BestsellerResponse.BestsellerItem.builder()
                            .rank(rank++)
                            .title(itemNode.path("title").asText())
                            .author(itemNode.path("author").asText())
                            .publisher(itemNode.path("publisher").asText())
                            .pubDate(itemNode.path("pubDate").asText())
                            .description(itemNode.path("description").asText())
                            .isbn(itemNode.path("isbn").asText())
                            .isbn13(itemNode.path("isbn13").asText())
                            .priceStandard(itemNode.path("priceStandard").asInt())
                            .priceSales(itemNode.path("priceSales").asInt())
                            .cover(itemNode.path("cover").asText())
                            .categoryId(itemNode.path("categoryId").asInt())
                            .categoryName(itemNode.path("categoryName").asText())
                            .link(itemNode.path("link").asText())
                            .build();

                    items.add(item);
                }
            }

            return BestsellerResponse.builder()
                    .title(title)
                    .totalResults(totalResults)
                    .startIndex(startIndex)
                    .itemsPerPage(itemsPerPage)
                    .pubDate(pubDate)
                    .items(items)
                    .build();

        } catch (Exception e) {
            log.error("응답 파싱 실패: {}", e.getMessage(), e);
            throw new RuntimeException("응답 파싱 중 오류가 발생했습니다", e);
        }
    }
}