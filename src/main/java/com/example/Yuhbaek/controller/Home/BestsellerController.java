package com.example.Yuhbaek.controller.Home;

import com.example.Yuhbaek.dto.Home.BestsellerRequest;
import com.example.Yuhbaek.dto.Home.BestsellerResponse;
import com.example.Yuhbaek.service.Home.BestsellerService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/bestsellers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "베스트셀러 API", description = "베스트셀러 조회 관련 API")
public class BestsellerController {

    private final BestsellerService bestsellerService;

    /**
     * 월간 베스트셀러 조회 (기본 10개)
     */
    @Operation(summary = "월간 베스트셀러 조회",
            description = "알라딘 API를 통해 월간 베스트셀러 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyBestsellers(
            @Parameter(description = "조회할 책 개수 (1-50)", example = "10")
            @RequestParam(defaultValue = "10") Integer maxResults,

            @Parameter(description = "시작 페이지 (1부터 시작)", example = "1")
            @RequestParam(defaultValue = "1") Integer start,

            @Parameter(description = "검색 대상 (Book, Foreign, eBook 등)", example = "Book")
            @RequestParam(defaultValue = "Book") String searchTarget) {

        try {
            BestsellerRequest request = BestsellerRequest.builder()
                    .queryType("Bestseller")
                    .maxResults(maxResults)
                    .start(start)
                    .searchTarget(searchTarget)
                    .build();

            BestsellerResponse response = bestsellerService.getMonthlyBestsellers(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("베스트셀러 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "베스트셀러 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 주간 베스트셀러 조회
     */
    @Operation(summary = "주간 베스트셀러 조회",
            description = "주간 베스트셀러를 조회합니다 (QueryType: BestsellerWeekly)")
    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklyBestsellers(
            @Parameter(description = "조회할 책 개수 (1-50)", example = "10")
            @RequestParam(defaultValue = "10") Integer maxResults) {

        try {
            BestsellerRequest request = BestsellerRequest.builder()
                    .queryType("BestsellerWeekly")
                    .maxResults(maxResults)
                    .searchTarget("Book")
                    .build();

            BestsellerResponse response = bestsellerService.getMonthlyBestsellers(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("주간 베스트셀러 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "주간 베스트셀러 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 신간 도서 조회
     */
    @Operation(summary = "신간 도서 조회",
            description = "주목할 만한 신간 도서를 조회합니다")
    @GetMapping("/new")
    public ResponseEntity<?> getNewBooks(
            @Parameter(description = "조회할 책 개수 (1-50)", example = "10")
            @RequestParam(defaultValue = "10") Integer maxResults) {

        try {
            BestsellerRequest request = BestsellerRequest.builder()
                    .queryType("ItemNewSpecial")
                    .maxResults(maxResults)
                    .searchTarget("Book")
                    .build();

            BestsellerResponse response = bestsellerService.getMonthlyBestsellers(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("신간 도서 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "신간 도서 조회 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }
}
