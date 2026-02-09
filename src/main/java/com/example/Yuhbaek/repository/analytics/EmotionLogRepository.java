package com.example.Yuhbaek.repository.analytics;


import com.example.Yuhbaek.entity.analytics.EmotionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {
}