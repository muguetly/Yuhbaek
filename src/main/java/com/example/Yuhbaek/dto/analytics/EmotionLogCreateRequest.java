package com.example.Yuhbaek.dto.analytics;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class EmotionLogCreateRequest {

    @NotNull
    private Long roomId;

    @NotNull
    private Integer emotionId;

    private LocalDateTime selectedAt;

    public Long getRoomId() { return roomId; }
    public Integer getEmotionId() { return emotionId; }
    public LocalDateTime getSelectedAt() { return selectedAt; }
}