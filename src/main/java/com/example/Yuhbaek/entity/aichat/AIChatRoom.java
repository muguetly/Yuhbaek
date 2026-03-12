package com.example.Yuhbaek.entity.aichat;

import com.example.Yuhbaek.entity.catalog.AllBook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ai_chat_room",
        indexes = {
                @Index(name = "idx_ai_room_user", columnList = "user_id"),
                @Index(name = "idx_ai_room_status", columnList = "status"),
                @Index(name = "idx_ai_room_last", columnList = "last_message_at")
        }
)
@Getter
@Setter
public class AIChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private AllBook book;

    @Column(name = "room_title", nullable = false)
    private String roomTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.IN_PROGRESS;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if ((roomTitle == null || roomTitle.isBlank()) && book != null) {
            roomTitle = book.getTitle();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}