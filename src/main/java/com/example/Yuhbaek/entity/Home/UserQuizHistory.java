package com.example.Yuhbaek.entity.Home;

import com.example.Yuhbaek.entity.SignUp.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_quiz_history",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "quiz_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuizHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizEntity quiz;

    @Column(nullable = false)
    private Boolean userAnswer; // 사용자가 선택한 답

    @Column(nullable = false)
    private Boolean isCorrect; // 정답 여부

    @Column(nullable = false)
    private LocalDate quizDate; // 퀴즈 푼 날짜

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime answeredAt; // 답변 시간
}
