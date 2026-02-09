package com.example.Yuhbaek.entity.analytics;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_session",
        indexes = {
                @Index(name = "idx_session_user_start", columnList = "userId, startedAt"),
                @Index(name = "idx_session_room", columnList = "roomId")
        }
)
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @Column(nullable = false)
    private Integer userMessageCount = 0;

    @Column(nullable = false)
    private Integer userCharCount = 0;

    protected ChatSession() {}

    public ChatSession(Long roomId, Long userId, LocalDateTime startedAt) {
        this.roomId = roomId;
        this.userId = userId;
        this.startedAt = startedAt;
    }

    public void end(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public void addUserMessage(int charCount) {
        this.userMessageCount++;
        this.userCharCount += Math.max(0, charCount);
    }

    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public Long getUserId() { return userId; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public Integer getUserMessageCount() { return userMessageCount; }
    public Integer getUserCharCount() { return userCharCount; }
}
