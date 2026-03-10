package com.example.Yuhbaek.entity.aichat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_chat_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AIChatSession {

    public enum Status { OPEN, CLOSED }
    public enum EndType { STOP, FINISH } // 여기까지 / 완독

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long roomId;

    /**
     * 세션 시작 시점 기준 "이전 마지막 메시지 id"
     * 범위 조회할 때: (startMessageId, endMessageId] 로 쓰면 깔끔함
     */
    private Long startMessageId;

    /** 세션 종료 버튼 누른 시점의 마지막 메시지 id */
    private Long endMessageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    private EndType endType;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    public static AIChatSession open(Long userId, Long roomId, Long startMessageId) {
        return AIChatSession.builder()
                .userId(userId)
                .roomId(roomId)
                .startMessageId(startMessageId)
                .status(Status.OPEN)
                .startedAt(LocalDateTime.now())
                .build();
    }

    public void close(Long endMessageId, EndType endType) {
        this.endMessageId = endMessageId;
        this.endType = endType;
        this.status = Status.CLOSED;
        this.endedAt = LocalDateTime.now();
    }
}
