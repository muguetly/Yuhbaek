package com.example.Yuhbaek.repository.aichat;

import com.example.Yuhbaek.entity.aichat.AIChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIChatMessageRepository extends JpaRepository<AIChatMessage, Long> {
    List<AIChatMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);
}