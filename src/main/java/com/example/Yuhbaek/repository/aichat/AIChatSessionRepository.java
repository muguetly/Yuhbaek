package com.example.Yuhbaek.repository.aichat;

import com.example.Yuhbaek.entity.aichat.AIChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AIChatSessionRepository extends JpaRepository<AIChatSession, Long> {
    Optional<AIChatSession> findTopByUserIdAndRoomIdAndStatusOrderByIdDesc(
            Long userId, Long roomId, AIChatSession.Status status
    );
}
