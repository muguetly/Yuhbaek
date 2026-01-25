package com.example.Yuhbaek.controller.Home;

import com.example.Yuhbaek.dto.Home.HomeScreenResponse;
import com.example.Yuhbaek.service.Home.HomeScreenService;
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
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "홈 화면 API", description = "홈 화면 전체 데이터 조회")
public class HomeScreenController {

    private final HomeScreenService homeScreenService;

    /**
     * 홈 화면 데이터 조회
     * - 사용자 정보
     * - 취향 기반 책 추천 (3개)
     * - 월간 베스트셀러 (추후 구현)
     * - 오늘의 퀴즈 (추후 구현)
     */
    @Operation(
            summary = "홈 화면 데이터 조회",
            description = "홈 화면에 표시할 모든 데이터를 한 번에 조회합니다 (사용자 정보, 추천 책, 베스트셀러, 퀴즈)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<HomeScreenResponse> getHomeScreen(
            @Parameter(description = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId) {

        log.info("홈 화면 조회 요청: userId={}", userId);

        HomeScreenResponse response = homeScreenService.getHomeScreen(userId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(500).body(response);
        }
    }
}
