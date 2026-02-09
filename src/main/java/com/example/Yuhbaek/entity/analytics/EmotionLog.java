package com.example.Yuhbaek.entity.analytics;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "emotion_log",
        indexes = {
                @Index(name = "idx_emotion_user_time", columnList = "userId, selectedAt"),
                @Index(name = "idx_emotion_room_time", columnList = "roomId, selectedAt")
        }
)
public class EmotionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer emotionId;

    @Column(nullable = false)
    private LocalDateTime selectedAt;

    protected EmotionLog() {}

    public EmotionLog(Long roomId, Long userId, Integer emotionId, LocalDateTime selectedAt) {
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
