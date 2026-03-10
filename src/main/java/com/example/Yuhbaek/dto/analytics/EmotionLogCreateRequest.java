package com.example.Yuhbaek.dto.analytics;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class EmotionLogCreateRequest {

    @NotNull
    private Long roomId;

    @NotNull
    private Integer emotionId;

    // 선택: 프론트가 시간 보내면 사용, 없으면 서버 now()
    private LocalDateTime selectedAt;

    public Long getRoomId() { return roomId; }
    public Integer getEmotionId() { return emotionId; }
    public LocalDateTime getSelectedAt() { return selectedAt; }
}
