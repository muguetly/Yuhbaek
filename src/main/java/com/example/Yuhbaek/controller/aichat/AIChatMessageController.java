package com.example.Yuhbaek.controller.aichat;

import com.example.Yuhbaek.controller.common.SessionAuthSupport;
import com.example.Yuhbaek.dto.aichat.MessageResponse;
import com.example.Yuhbaek.dto.aichat.SendMessageRequest;
import com.example.Yuhbaek.service.aichat.AIChatMessageService;
import com.example.Yuhbaek.service.aichat.AIChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aichat/rooms/{roomId}/messages")
@RequiredArgsConstructor
@Tag(name = "AI 채팅 메시지 API", description = "AI 채팅 메시지 전송 및 메시지 목록 조회 API")
public class AIChatMessageController extends SessionAuthSupport {

    private final AIChatMessageService messageService;
    private final AIChatSessionService sessionService;

    @Operation(summary = "메시지 전송", description = "사용자 메시지를 저장하고 AI 응답을 생성합니다.")
    @PostMapping
    public ResponseEntity<?> send(
            @PathVariable Long roomId,
            @Valid @RequestBody SendMessageRequest request,
            HttpSession session
    ) {
        Long userId;
        try {
            userId = requireLogin(session);
        } catch (IllegalStateException e) {
            return unauthorized();
        }

        // OPEN 세션이 없으면 자동 시작
        sessionService.startIfAbsent(userId, roomId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", messageService.sendUserMessage(userId, roomId, request.getContent())
        ));
    }

    @Operation(summary = "메시지 목록 조회", description = "이전 메시지를 조회해 채팅을 이어서 진행할 수 있습니다.")
    @GetMapping
    public ResponseEntity<?> list(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "50") int size,
            HttpSession session
    ) {
        try {
            Long userId = requireLogin(session);
            List<MessageResponse> messages = messageService.getMessages(userId, roomId, size);
            return ResponseEntity.ok(Map.of("success", true, "data", messages));
        } catch (IllegalStateException e) {
            return unauthorized();
        }
    }
}