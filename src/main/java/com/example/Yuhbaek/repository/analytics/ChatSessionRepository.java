package com.example.Yuhbaek.repository.analytics;

import com.example.Yuhbaek.entity.analytics.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
}