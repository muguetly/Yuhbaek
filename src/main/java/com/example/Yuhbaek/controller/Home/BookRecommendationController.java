package com.example.Yuhbaek.controller.Home;

import com.example.Yuhbaek.dto.Home.BookRecommendationResponse;
import com.example.Yuhbaek.service.Home.BookRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "책 추천 API", description = "사용자 취향 기반 책 추천 관련 API")
public class BookRecommendationController {

    private final BookRecommendationService recommendationService;

    /**
     * 사용자 취향 기반 책 추천 (3권)
     */
    @Operation(
            summary = "취향 기반 책 추천",
            description = "사용자의 취향 설문을 기반으로 맞춤 책 3권을 추천합니다. 취향 정보가 없으면 인기 도서를 추천합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<BookRecommendationResponse> getRecommendations(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId) {

        log.info("책 추천 요청: userId={}", userId);

        BookRecommendationResponse response = recommendationService.recommendBooks(userId);

        if (response.isSuccess()) {
            log.info("책 추천 성공: userId={}, count={}", userId, response.getRecommendations().size());
            return ResponseEntity.ok(response);
        } else {
            log.error("책 추천 실패: userId={}, message={}", userId, response.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
