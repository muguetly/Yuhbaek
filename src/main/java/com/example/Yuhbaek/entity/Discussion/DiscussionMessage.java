package com.example.Yuhbaek.entity.Discussion;

import com.example.Yuhbaek.entity.SignUp.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "discussion_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_room_id", nullable = false)
    private BookDiscussionRoom discussionRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type = MessageType.CHAT; // 메시지 타입

    @Column(nullable = false, length = 2000)
    private String content; // 메시지 내용

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 메시지 타입 Enum
    public enum MessageType {
        CHAT,   // 일반 채팅
        ENTER,  // 입장 알림
        LEAVE,  // 퇴장 알림
        SYSTEM  // 시스템 메시지
    }
}