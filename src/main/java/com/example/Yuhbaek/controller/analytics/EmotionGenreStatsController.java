package com.example.Yuhbaek.controller.analytics;

import com.example.Yuhbaek.dto.analytics.EmotionGenreStatsResponse;
import com.example.Yuhbaek.service.analytics.EmotionGenreStatsService;
import com.example.Yuhbaek.service.analytics.EmotionGenreStatsService.Mode;
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
@Tag(name = "Analytics API", description = "사용자 통계 관련 API")
public class EmotionGenreStatsController {

    private final EmotionGenreStatsService service;

    @Operation(summary = "감정 ↔ 장르 분포(도넛)", description = "mode에 따라 감정->장르 또는 장르->감정 분포를 반환합니다")
    @GetMapping("/emotion-genre")
    public ResponseEntity<?> getEmotionGenre(
            @RequestParam Mode mode,
            @RequestParam(required = false) Integer emotionId,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpSession session
    ) {
        Long userPk = (Long) session.getAttribute("id");
        if (userPk == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            EmotionGenreStatsResponse data = service.getStats(userPk, mode, emotionId, genre, year, month);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
