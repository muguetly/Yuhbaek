package com.example.Yuhbaek.entity.Discussion;

import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_discussion_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDiscussionRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 선택한 도서 정보
    @Column(nullable = false, length = 500)
    private String bookTitle;

    @Column(nullable = false, length = 500)
    private String bookAuthor;

    @Column(length = 100)
    private String bookIsbn;

    @Column(length = 1000)
    private String bookCover;

    @Column(length = 500)
    private String bookPublisher;

    // 토론방 정보
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer maxParticipants = 4;

    @Column(nullable = false)
    private Integer currentParticipants = 0;

    @Column(nullable = false)
    private LocalDateTime discussionStartTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscussionStatus status = DiscussionStatus.WAITING;

    // ✅ 새로 추가: 대화 규칙/스타일 (최소 1개, 최대 4개)
    @Column(columnDefinition = "JSON")
    private String discussionRules;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private UserEntity host;

    @OneToMany(mappedBy = "discussionRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DiscussionParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "discussionRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DiscussionMessage> messages = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 토론방 상태 Enum
    public enum DiscussionStatus {
        WAITING,     // 대기 중 (시작 시간 전 또는 준비 중)
        IN_PROGRESS, // 진행 중 (시작됨)
        FINISHED     // 종료됨
    }

    /**
     * ✅ 대화 규칙 설정 메서드
     */
    public void setDiscussionRules(List<String> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("대화 규칙은 최소 1개 이상 설정해야 합니다");
        }
        if (rules.size() > 4) {
            throw new IllegalArgumentException("대화 규칙은 최대 4개까지 설정 가능합니다");
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.discussionRules = objectMapper.writeValueAsString(rules);
        } catch (Exception e) {
            throw new RuntimeException("규칙 변환 중 오류 발생", e);
        }
    }

    /**
     * ✅ 대화 규칙 조회 메서드
     */
    public List<String> getDiscussionRulesList() {
        if (discussionRules == null || discussionRules.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(discussionRules, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 참여자 증가
     */
    public void incrementParticipants() {
        this.currentParticipants++;
    }

    /**
     * 참여자 감소
     */
    public void decrementParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    /**
     * 방이 가득 찼는지 확인
     */
    public boolean isFull() {
        return this.currentParticipants >= this.maxParticipants;
    }

    /**
     * 예약 시간이 지났는지 확인
     */
    public boolean isScheduledTimeReached() {
        return LocalDateTime.now().isAfter(this.discussionStartTime) ||
                LocalDateTime.now().isEqual(this.discussionStartTime);
    }

    /**
     * ✅ 모든 참여자가 준비 완료했는지 확인
     */
    public boolean areAllParticipantsReady() {
        // 활성 참여자가 없으면 false
        if (this.participants.isEmpty()) {
            return false;
        }

        // 활성 참여자 중 한 명이라도 준비 안 됐으면 false
        long activeCount = this.participants.stream()
                .filter(DiscussionParticipant::getIsActive)
                .count();

        long readyCount = this.participants.stream()
                .filter(DiscussionParticipant::getIsActive)
                .filter(DiscussionParticipant::getIsReady)
                .count();

        // 모든 활성 참여자가 준비 완료
        return activeCount > 0 && activeCount == readyCount;
    }

    /**
     * ✅ 토론 시작 가능 여부 확인
     * 조건 1: 모든 참여자 준비 완료
     * 조건 2: 예약 시간 도달
     */
    public boolean canStart() {
        return areAllParticipantsReady() || isScheduledTimeReached();
    }

    /**
     * 토론방 상태 업데이트
     */
    public void updateStatus() {
        if (this.status == DiscussionStatus.FINISHED) {
            return; // 이미 종료된 방은 상태 변경 안함
        }

        // 시작 가능 조건 확인
        if (canStart()) {
            this.status = DiscussionStatus.IN_PROGRESS;
        } else {
            this.status = DiscussionStatus.WAITING;
        }
    }

    /**
     * ✅ 강제 시작 (방장 권한)
     */
    public void forceStart() {
        this.status = DiscussionStatus.IN_PROGRESS;
    }

    /**
     * 토론방 종료
     */
    public void finish() {
        this.status = DiscussionStatus.FINISHED;
    }
}