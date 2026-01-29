package com.example.Yuhbaek.dto.aichat;

import com.example.Yuhbaek.entity.aichat.MessageRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageResponse {
    private Long messageId;
    private MessageRole role;
    private String content;
    private LocalDateTime createdAt;
}