package com.example.Yuhbaek.controller.analytics;

import com.example.Yuhbaek.dto.analytics.ThinkingStyleStatsResponse;
import com.example.Yuhbaek.service.analytics.ThinkingStyleStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "통계/분석 API", description = "사고 스타일(자가진단) 통계")
public class ThinkingStyleStatsController {

    private final ThinkingStyleStatsService service;

    @Operation(summary = "사고 스타일 점수 통계(평균)", description = "기간 필터(연/월)로 사고 스타일 평균 점수를 반환합니다")
    @GetMapping("/thinking-style")
    public ResponseEntity<?> getThinkingStyle(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("id");

        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        ThinkingStyleStatsResponse data = service.getStats(userId, year, month);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
