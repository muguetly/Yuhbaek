package com.example.Yuhbaek.repository.analytics;

import com.example.Yuhbaek.entity.analytics.ThinkingStyleScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ThinkingStyleScoreRepository extends JpaRepository<ThinkingStyleScore, Long> {

    @Query("""
        SELECT
            AVG(t.critic)  as critic,
            AVG(t.emotion) as emotion,
            AVG(t.analysis) as analysis,
            AVG(t.empathy) as empathy,
            AVG(t.creative) as creative
        FROM ThinkingStyleScore t
        WHERE t.userId = :userId
          AND t.createdAt BETWEEN :start AND :end
    """)
    ThinkingStyleAvgRow avgScores(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
