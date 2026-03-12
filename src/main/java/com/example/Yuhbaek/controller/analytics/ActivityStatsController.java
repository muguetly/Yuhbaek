package com.example.Yuhbaek.controller.analytics;

import com.example.Yuhbaek.dto.analytics.HourlyActivityDto;
import com.example.Yuhbaek.service.analytics.ActivityStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics API", description = "사용자 통계 관련 API")
public class ActivityStatsController {

    private final ActivityStatsService activityStatsService;

    @Operation(summary = "채팅 시간대 & 참여도 통계")
    @GetMapping("/activity")
    public ResponseEntity<?> getActivityStats(
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

        List<HourlyActivityDto> data =
                activityStatsService.getHourlyStats(userId, year, month);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
