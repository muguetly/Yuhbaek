package com.example.Yuhbaek.repository.aichat;

import com.example.Yuhbaek.entity.aichat.AIChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AIChatMessageRepository extends JpaRepository<AIChatMessage, Long> {
    List<AIChatMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    @Query("""
    SELECT 
        HOUR(m.createdAt) as hour,
        COUNT(m) as messageCount,
        SUM(LENGTH(m.content)) as charCount
    FROM AIChatMessage m
    WHERE m.role = com.example.Yuhbaek.entity.aichat.MessageRole.USER
      AND m.roomId IN (
          SELECT r.roomId
          FROM AIChatRoom r
          WHERE r.userId = :userId
      )
      AND m.createdAt BETWEEN :start AND :end
    GROUP BY HOUR(m.createdAt)
    ORDER BY HOUR(m.createdAt)
    
""")
    List<Object[]> aggregateUserActivityByHour(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}