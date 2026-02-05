package com.example.Yuhbaek.entity.MyPage;

import com.example.Yuhbaek.entity.SignUp.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reading_note")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReadingNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 20)
    private String bookIsbn;

    @Column(nullable = false, length = 500)
    private String bookTitle;

    @Column(length = 200)
    private String author;

    @Column(length = 500)
    private String coverImage;

    @Column(length = 200)
    private String publisher;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String memorableQuote;  // 기억에 남는 문구

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 연관관계 편의 메서드
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * 문구 수정
     */
    public void updateQuote(String memorableQuote) {
        this.memorableQuote = memorableQuote;
    }
}