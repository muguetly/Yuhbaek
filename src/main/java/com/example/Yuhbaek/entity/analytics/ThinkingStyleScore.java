package com.example.Yuhbaek.entity.analytics;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "thinking_style_score",
        indexes = {
                @Index(name = "idx_style_user_time", columnList = "userId, createdAt")
        }
)
public class ThinkingStyleScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Integer critic;

    @Column(nullable = false)
    private Integer emotion;

    @Column(nullable = false)
    private Integer analysis;

    @Column(nullable = false)
    private Integer empathy;

    @Column(nullable = false)
    private Integer creative;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected ThinkingStyleScore() {}

    public ThinkingStyleScore(
            Long userId,
            Long roomId,
            Integer critic,
            Integer emotion,
            Integer analysis,
            Integer empathy,
            Integer creative,
            LocalDateTime createdAt
    ) {
        this.userId = userId;
        this.roomId = roomId;
        this.critic = critic;
        this.emotion = emotion;
        this.analysis = analysis;
        this.empathy = empathy;
        this.creative = creative;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getRoomId() { return roomId; }
    public Integer getCritic() { return critic; }
    public Integer getEmotion() { return emotion; }
    public Integer getAnalysis() { return analysis; }
    public Integer getEmpathy() { return empathy; }
    public Integer getCreative() { return creative; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
