package com.example.Yuhbaek.entity.aichat;

import com.example.Yuhbaek.entity.catalog.AllBook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ai_chat_room",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_ai_room_user_book", columnNames = {"userId", "book_id"})
        },
        indexes = {
                @Index(name = "idx_ai_room_user", columnList = "userId"),
                @Index(name = "idx_ai_room_status", columnList = "status"),
                @Index(name = "idx_ai_room_last", columnList = "lastMessageAt")
        }
)
@Getter @Setter
public class AIChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    // 지금은 임시로 userId를 헤더로 받음. 나중에 인증 붙이면 교체
    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private AllBook book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.IN_PROGRESS;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime lastMessageAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}