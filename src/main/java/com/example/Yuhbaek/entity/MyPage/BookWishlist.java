package com.example.Yuhbaek.entity.MyPage;

import com.example.Yuhbaek.entity.SignUp.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_wishlist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BookWishlist {

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

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 연관관계 편의 메서드
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }
}