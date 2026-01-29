package com.example.Yuhbaek.controller.Home;

import com.example.Yuhbaek.dto.Home.BookResponse;
import com.example.Yuhbaek.dto.Home.BookSearchRequest;
import com.example.Yuhbaek.dto.Home.BookSearchResponse;
import com.example.Yuhbaek.service.Home.BookSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "책 검색 API", description = "책 검색 및 조회 관련 API")
public class BookSearchController {

    private final BookSearchService bookSearchService;

    /**
     * 책 검색
     */
    @Operation(summary = "책 검색", description = "키워드로 책을 검색합니다 (카카오 API 사용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(
            @Parameter(description = "검색어", required = true, example = "미움받을 용기")
            @RequestParam String query,

            @Parameter(description = "검색 대상 (all, title, isbn, publisher, person)", example = "all")
            @RequestParam(defaultValue = "all") String target,

            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "페이지당 결과 수 (최대 50)", example = "10")
            @RequestParam(defaultValue = "10") Integer size,

            @Parameter(description = "정렬 방식 (accuracy: 정확도순, recency: 최신순)", example = "accuracy")
            @RequestParam(defaultValue = "accuracy") String sort) {

        try {
            BookSearchRequest request = BookSearchRequest.builder()
                    .query(query)
                    .target(target)
                    .page(page)
                    .size(size)
                    .sort(sort)
                    .build();

            BookSearchResponse searchResponse = bookSearchService.searchBooks(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", searchResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("책 검색 실패: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "책 검색 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * ISBN으로 책 상세 조회
     */
    @Operation(summary = "ISBN으로 책 조회", description = "ISBN으로 특정 책의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "책을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<?> getBookByIsbn(
            @Parameter(description = "ISBN", required = true, example = "9788996991342")
            @PathVariable String isbn) {

        try {
            BookResponse book = bookSearchService.getBookByIsbn(isbn);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", book);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("책 조회 실패: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(404).body(response);

        } catch (Exception e) {
            log.error("책 조회 중 오류: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "책 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 인기 검색어 + 최신 검색어 조회
     */
    @Operation(summary = "인기 및 최신 검색어 조회", description = "인기 검색어와 최신 검색어를 함께 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/keywords")
    public ResponseEntity<?> getSearchKeywords(
            @Parameter(description = "조회할 검색어 개수", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {

        try {
            List<com.example.Yuhbaek.dto.Home.SearchKeywordResponse> popularKeywords =
                    bookSearchService.getPopularKeywords(limit);
            List<com.example.Yuhbaek.dto.Home.SearchKeywordResponse> recentKeywords =
                    bookSearchService.getRecentKeywords(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "popularKeywords", popularKeywords,
                    "recentKeywords", recentKeywords
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("검색어 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "검색어 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 인기 검색어
     */
    @Operation(summary = "인기 검색어 조회 (단독)", description = "인기 검색어만 조회합니다")
    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingKeywords(
            @Parameter(description = "조회할 검색어 개수", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {

        try {
            List<com.example.Yuhbaek.dto.Home.SearchKeywordResponse> keywords =
                    bookSearchService.getPopularKeywords(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", keywords);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("인기 검색어 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "인기 검색어 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(response);
        }
    }
}