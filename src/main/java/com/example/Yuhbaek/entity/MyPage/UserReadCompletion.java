package com.example.Yuhbaek.entity.MyPage;

import com.example.Yuhbaek.entity.SignUp.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_read_completions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_isbn"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReadCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 책 정보 스냅샷 (외부 API 데이터라 별도 저장)
    @Column(name = "book_isbn", nullable = false, length = 100)
    private String bookIsbn;

    @Column(name = "book_title", nullable = false, length = 500)
    private String bookTitle;

    @Column(name = "book_author", length = 500)
    private String bookAuthor;

    @Column(name = "book_cover", length = 1000)
    private String bookCover;

    // 완독 경로: AI_CHAT or GROUP_CHAT
    @Enumerated(EnumType.STRING)
    @Column(name = "completion_type", nullable = false)
    private CompletionType completionType;

    @CreationTimestamp
    @Column(name = "completed_at", nullable = false, updatable = false)
    private LocalDateTime completedAt;

    public enum CompletionType {
        AI_CHAT,    // AI 채팅으로 완독
        GROUP_CHAT  // 그룹 토론으로 완독
    }
}