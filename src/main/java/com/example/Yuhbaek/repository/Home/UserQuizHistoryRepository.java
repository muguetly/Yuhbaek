package com.example.Yuhbaek.repository.Home;

import com.example.Yuhbaek.entity.Home.UserQuizHistory;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizHistoryRepository extends JpaRepository<UserQuizHistory, Long> {

    /**
     * 특정 날짜에 사용자가 퀴즈를 풀었는지 확인
     */
    boolean existsByUserAndQuizDate(UserEntity user, LocalDate quizDate);

    /**
     * 특정 날짜의 사용자 퀴즈 기록 조회
     */
    Optional<UserQuizHistory> findByUserAndQuizDate(UserEntity user, LocalDate quizDate);

    /**
     * 사용자의 전체 퀴즈 기록 조회
     */
    List<UserQuizHistory> findByUserOrderByQuizDateDesc(UserEntity user);

    /**
     * 사용자의 정답 개수 조회
     */
    @Query("SELECT COUNT(h) FROM UserQuizHistory h WHERE h.user = :user AND h.isCorrect = true")
    Long countCorrectAnswers(@Param("user") UserEntity user);

    /**
     * 사용자의 전체 퀴즈 개수 조회
     */
    @Query("SELECT COUNT(h) FROM UserQuizHistory h WHERE h.user = :user")
    Long countTotalQuizzes(@Param("user") UserEntity user);

    /**
     * 사용자의 연속 정답 일수
     */
    @Query(value = "SELECT COUNT(*) FROM (" +
            "  SELECT quiz_date, " +
            "         ROW_NUMBER() OVER (ORDER BY quiz_date DESC) as rn, " +
            "         DATE_SUB(quiz_date, INTERVAL ROW_NUMBER() OVER (ORDER BY quiz_date DESC) DAY) as grp " +
            "  FROM user_quiz_history " +
            "  WHERE user_id = :userId AND is_correct = true " +
            ") as streaks " +
            "WHERE grp = (SELECT DATE_SUB(quiz_date, INTERVAL ROW_NUMBER() OVER (ORDER BY quiz_date DESC) DAY) " +
            "             FROM user_quiz_history " +
            "             WHERE user_id = :userId AND is_correct = true " +
            "             ORDER BY quiz_date DESC LIMIT 1)",
            nativeQuery = true)
    Integer calculateCurrentStreak(@Param("userId") Long userId);
}