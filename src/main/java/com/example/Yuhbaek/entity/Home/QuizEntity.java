package com.example.Yuhbaek.entity.Home;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "daily_quiz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false)
    private Boolean answer; // true = O, false = X

    @Column(length = 500)
    private String explanation; // 정답 해설

    @Column(nullable = false)
    private String category; // 카테고리 (예: 문학, 역사, 과학 등)

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true; // 활성화 여부

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}