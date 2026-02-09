package com.example.Yuhbaek.repository.analytics;

import com.example.Yuhbaek.entity.analytics.ThinkingStyleScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ThinkingStyleScoreRepository extends JpaRepository<ThinkingStyleScore, Long> {

    @Query("""
        SELECT
            AVG(t.critic),
            AVG(t.emotion),
            AVG(t.analysis),
            AVG(t.empathy),
            AVG(t.creative)
        FROM ThinkingStyleScore t
        WHERE t.userId = :userId
          AND t.createdAt BETWEEN :start AND :end
    """)
    Object[] avgScores(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
