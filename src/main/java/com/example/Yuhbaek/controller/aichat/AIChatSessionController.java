package com.example.Yuhbaek.controller.aichat;

import com.example.Yuhbaek.controller.common.SessionAuthSupport;
import com.example.Yuhbaek.entity.analytics.ThinkingStyleScore;
import com.example.Yuhbaek.repository.aichat.AIChatRoomRepository;
import com.example.Yuhbaek.service.aichat.AIChatSessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aichat/rooms/{roomId}")
@Tag(name = "AI 세션 API", description = "여기까지/완독 버튼용 세션 종료 및 사고스타일 분석 저장")
public class AIChatSessionController extends SessionAuthSupport {

    private final AIChatRoomRepository roomRepository;
    private final AIChatSessionService sessionService;

    private void requireRoomOwner(Long roomId, Long userId) {
        roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 없거나 권한이 없습니다."));
    }

    /** (선택) 세션 시작 버튼이 필요하면 사용 */
    @PostMapping("/session/start")
    public ResponseEntity<?> start(
            @PathVariable Long roomId,
            HttpSession session
    ) {
        Long userId;
        try {
            userId = requireLogin(session);
        } catch (IllegalStateException e) {
            return unauthorized();
        }

        requireRoomOwner(roomId, userId);

        Long sessionId = sessionService.startIfAbsent(userId, roomId);
        return ResponseEntity.ok(Map.of("success", true, "sessionId", sessionId));
    }

    /** ✅ 기존 URL 유지: 여기까지 버튼 */
    @PostMapping("/end-session")
    public ResponseEntity<?> endSession(
            @PathVariable Long roomId,
            HttpSession session
    ) {
        Long userId;
        try {
            userId = requireLogin(session);
        } catch (IllegalStateException e) {
            return unauthorized();
        }

        requireRoomOwner(roomId, userId);

        try {
            ThinkingStyleScore saved = sessionService.end(userId, roomId);
            return ResponseEntity.ok(Map.of("success", true, "data", Map.of(
                    "roomId", roomId,
                    "critic", saved.getCritic(),
                    "emotion", saved.getEmotion(),
                    "analysis", saved.getAnalysis(),
                    "empathy", saved.getEmpathy(),
                    "creative", saved.getCreative(),
                    "createdAt", saved.getCreatedAt()
            )));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /** ✅ 완독 버튼: 세션 종료 + 분석 저장 + 완독 처리 */
    @PostMapping("/finish-session")
    public ResponseEntity<?> finishSession(
            @PathVariable Long roomId,
            HttpSession session
    ) {
        Long userId;
        try {
            userId = requireLogin(session);
        } catch (IllegalStateException e) {
            return unauthorized();
        }

        requireRoomOwner(roomId, userId);

        try {
            ThinkingStyleScore saved = sessionService.finish(userId, roomId);
            return ResponseEntity.ok(Map.of("success", true, "data", Map.of(
                    "roomId", roomId,
                    "critic", saved.getCritic(),
                    "emotion", saved.getEmotion(),
                    "analysis", saved.getAnalysis(),
                    "empathy", saved.getEmpathy(),
                    "creative", saved.getCreative(),
                    "createdAt", saved.getCreatedAt()
            )));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
