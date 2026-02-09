package com.example.Yuhbaek.dto.analytics;

import java.time.LocalDateTime;

public class EmotionLogCreateResponse {

    private final Long id;
    private final Long roomId;
    private final Long userId;
    private final Integer emotionId;
    private final LocalDateTime selectedAt;

    public EmotionLogCreateResponse(Long id, Long roomId, Long userId, Integer emotionId, LocalDateTime selectedAt) {
        this.id = id;
        this.roomId = roomId;
        this.userId = userId;
        this.emotionId = emotionId;
        this.selectedAt = selectedAt;
    }

    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public Long getUserId() { return userId; }
    public Integer getEmotionId() { return emotionId; }
    public LocalDateTime getSelectedAt() { return selectedAt; }
}
