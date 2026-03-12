package com.example.Yuhbaek.controller.aichat;

import com.example.Yuhbaek.controller.common.SessionAuthSupport;
import com.example.Yuhbaek.entity.analytics.ThinkingStyleScore;
import com.example.Yuhbaek.repository.aichat.AIChatRoomRepository;
import com.example.Yuhbaek.service.aichat.AIChatSessionService;
import com.example.Yuhbaek.entity.aichat.AIChatRoom;
import com.example.Yuhbaek.entity.aichat.RoomStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aichat/rooms/{roomId}")
@Tag(name = "AI 채팅 세션 API", description = "AI 채팅 세션 시작, 중간 저장 종료, 완독 종료 API")
public class AIChatSessionController extends SessionAuthSupport {

    private final AIChatRoomRepository roomRepository;
    private final AIChatSessionService sessionService;

    private void requireRoomOwner(Long roomId, Long userId) {
        roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 없거나 권한이 없습니다."));
    }

    /**
     * 세션 시작
     * - 이어하기 포함
     * - 아직 OPEN 세션이 있으면 재사용
     * - 없으면 새 세션 생성
     */
    @Operation(summary = "세션 시작", description = "AI 채팅 세션을 시작하고 분석 기준 시점을 기록합니다.")
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

    /**
     * 여기까지 읽기
     * - 현재 세션 종료
     * - 사고 스타일 분석 저장
     * - 채팅방 상태는 IN_PROGRESS 유지
     */
    @Operation(summary = "중간 저장 후 세션 종료", description = "여기까지 읽기 시점의 세션을 종료하고 사고 스타일 분석 결과를 저장합니다.")
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

    /**
     * 완독 종료
     * - 현재 세션 종료
     * - 사고 스타일 분석 저장
     * - 채팅방 상태를 FINISHED로 변경
     */
    @Operation(summary = "완독 후 세션 종료", description = "완독 시점의 세션을 종료하고 사고 스타일 분석 결과를 저장한 뒤 채팅방도 완료 처리합니다.")
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
            AIChatRoom room = roomRepository.findByRoomIdAndUserId(roomId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 없거나 권한이 없습니다."));

            boolean alreadyCompletedBook =
                    roomRepository.existsByUserIdAndBook_IdAndStatusAndRoomIdNot(
                            userId,
                            room.getBook().getId(),
                            RoomStatus.FINISHED,
                            roomId
                    );

            ThinkingStyleScore saved = sessionService.finish(userId, roomId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "roomId", roomId,
                            "critic", saved.getCritic(),
                            "emotion", saved.getEmotion(),
                            "analysis", saved.getAnalysis(),
                            "empathy", saved.getEmpathy(),
                            "creative", saved.getCreative(),
                            "createdAt", saved.getCreatedAt(),
                            "alreadyCompletedBook", alreadyCompletedBook,
                            "message", alreadyCompletedBook ? "이미 완독한 도서입니다." : "완독 처리되었습니다."
                    )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}