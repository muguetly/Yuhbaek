package com.example.Yuhbaek.dto.aichat;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SendMessageRequest {
    @NotBlank
    private String content;
}