package com.example.Yuhbaek.service.analytics;

import com.example.Yuhbaek.repository.analytics.EmotionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmotionGateService {

    private final EmotionLogRepository emotionLogRepository;

    public boolean hasTodayEmotion(Long userId, Long roomId) {
        LocalDate today = LocalDate.now(); // 서버 타임존(Asia/Seoul이면 OK)
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        return emotionLogRepository.existsByUserIdAndRoomIdAndSelectedAtBetween(userId, roomId, start, end);
    }
}
