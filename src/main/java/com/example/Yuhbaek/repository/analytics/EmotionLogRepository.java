package com.example.Yuhbaek.repository.analytics;

import com.example.Yuhbaek.entity.analytics.EmotionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {

    // ✅ (A) 감정 선택 → 장르 분포
    @Query("""
        SELECT 
            COALESCE(b.genre, '기타') as genre,
            COUNT(e) as cnt
        FROM EmotionLog e
        JOIN AIChatRoom r ON r.roomId = e.roomId
        JOIN r.book b
        WHERE e.userId = :userId
          AND e.emotionId = :emotionId
          AND e.selectedAt BETWEEN :start AND :end
        GROUP BY COALESCE(b.genre, '기타')
        ORDER BY cnt DESC
    """)
    List<Object[]> countGenresByEmotion(
            @Param("userId") Long userId,
            @Param("emotionId") Integer emotionId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // ✅ (B) 장르 선택 → 감정 분포
    @Query("""
        SELECT 
            e.emotionId as emotionId,
            COUNT(e) as cnt
        FROM EmotionLog e
        JOIN AIChatRoom r ON r.roomId = e.roomId
        JOIN r.book b
        WHERE e.userId = :userId
          AND COALESCE(b.genre, '기타') = :genre
          AND e.selectedAt BETWEEN :start AND :end
        GROUP BY e.emotionId
        ORDER BY cnt DESC
    """)
    List<Object[]> countEmotionsByGenre(
            @Param("userId") Long userId,
            @Param("genre") String genre,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
