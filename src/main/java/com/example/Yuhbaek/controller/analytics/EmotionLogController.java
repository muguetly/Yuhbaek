package com.example.Yuhbaek.controller.analytics;

import com.example.Yuhbaek.dto.analytics.EmotionLogCreateRequest;
import com.example.Yuhbaek.dto.analytics.EmotionLogCreateResponse;
import com.example.Yuhbaek.entity.analytics.EmotionLog;
import com.example.Yuhbaek.service.analytics.EmotionLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "통계/분석 API", description = "자가진단/마이페이지 통계를 위한 로그 저장 API")
public class EmotionLogController {

    private final EmotionLogService emotionLogService;

    @Operation(summary = "오늘의 기분 선택 저장", description = "채팅방에서 사용자가 선택한 감정을 저장합니다")
    @PostMapping("/emotions")
    public ResponseEntity<?> saveEmotion(@Valid @RequestBody EmotionLogCreateRequest request, HttpSession session) {
        Long loginUserPk = (Long) session.getAttribute("id");

        if (loginUserPk == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        EmotionLog saved = emotionLogService.save(loginUserPk, request);

        EmotionLogCreateResponse body = new EmotionLogCreateResponse(
                saved.getId(),
                saved.getRoomId(),
                saved.getUserId(),
                saved.getEmotionId(),
                saved.getSelectedAt()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", body);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
