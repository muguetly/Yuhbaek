package com.example.Yuhbaek.entity.Discussion;

import com.example.Yuhbaek.entity.SignUp.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "discussion_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionParticipant {

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
    private ParticipantRole role = ParticipantRole.MEMBER; // 역할 (방장/참여자)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt; // 입장 시간

    @Column
    private LocalDateTime leftAt; // 퇴장 시간

    @Column(nullable = false)
    private Boolean isActive = true; // 현재 참여 중인지 여부

    // ✅ 준비 상태 추가
    @Column(nullable = false)
    @Builder.Default
    private Boolean isReady = false; // 준비 완료 여부

    // 역할 Enum
    public enum ParticipantRole {
        HOST,   // 방장
        MEMBER  // 일반 참여자
    }

    /**
     * 참여자 퇴장 처리
     */
    public void leave() {
        this.isActive = false;
        this.leftAt = LocalDateTime.now();
    }

    /**
     * 준비 상태 토글
     */
    public void toggleReady() {
        this.isReady = !this.isReady;
    }

    /**
     * 준비 완료
     */
    public void setReady(boolean ready) {
        this.isReady = ready;
    }
}