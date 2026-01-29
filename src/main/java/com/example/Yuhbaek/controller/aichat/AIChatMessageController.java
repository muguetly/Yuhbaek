package com.example.Yuhbaek.controller.aichat;

import com.example.Yuhbaek.dto.aichat.MessageResponse;
import com.example.Yuhbaek.dto.aichat.SendMessageRequest;
import com.example.Yuhbaek.service.aichat.AIChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aichat/rooms/{roomId}/messages")
@RequiredArgsConstructor
@Tag(name = "AI 메시지 API", description = "AI 채팅 메시지 저장/조회 API")
public class AIChatMessageController {

    private final AIChatMessageService messageService;

    @Operation(summary = "사용자 메시지 저장")
    @PostMapping
    public ResponseEntity<MessageResponse> send(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long roomId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        return ResponseEntity.ok(
                messageService.sendUserMessage(userId, roomId, request.getContent())
        );
    }

    @Operation(summary = "메시지 목록 조회(이어하기)")
    @GetMapping
    public ResponseEntity<List<MessageResponse>> list(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(
                messageService.getMessages(userId, roomId, size)
        );
    }
}