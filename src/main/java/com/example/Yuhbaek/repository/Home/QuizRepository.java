package com.example.Yuhbaek.repository.Home;

import com.example.Yuhbaek.entity.Home.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Long> {

    /**
     * 활성화된 퀴즈 목록 조회
     */
    List<QuizEntity> findByIsActiveTrue();

    /**
     * 랜덤으로 1개의 퀴즈 가져오기
     */
    @Query(value = "SELECT * FROM daily_quiz WHERE is_active = true ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<QuizEntity> findRandomQuiz();

    /**
     * 카테고리별 퀴즈 조회
     */
    List<QuizEntity> findByCategoryAndIsActiveTrue(String category);
}