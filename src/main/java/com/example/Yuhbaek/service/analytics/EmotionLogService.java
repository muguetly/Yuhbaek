package com.example.Yuhbaek.service.analytics;

import com.example.Yuhbaek.dto.analytics.EmotionLogCreateRequest;
import com.example.Yuhbaek.entity.analytics.EmotionLog;
import com.example.Yuhbaek.repository.analytics.EmotionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmotionLogService {

    private final EmotionLogRepository emotionLogRepository;

    public EmotionLog save(Long loginUserPk, EmotionLogCreateRequest request) {
        LocalDateTime selectedAt = request.getSelectedAt() != null ? request.getSelectedAt() : LocalDateTime.now();

        EmotionLog log = new EmotionLog(
                request.getRoomId(),
                loginUserPk,
                request.getEmotionId(),
                selectedAt
        );

        return emotionLogRepository.save(log);
    }
}
