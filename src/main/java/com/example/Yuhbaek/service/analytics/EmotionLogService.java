package com.example.Yuhbaek.service.analytics;

import com.example.Yuhbaek.dto.analytics.EmotionLogCreateRequest;
import com.example.Yuhbaek.entity.analytics.EmotionLog;
import com.example.Yuhbaek.entity.analytics.EmotionType;
import com.example.Yuhbaek.repository.analytics.EmotionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmotionLogService {

    private final EmotionLogRepository emotionLogRepository;

    @Transactional
    public EmotionLog save(Long loginUserPk, EmotionLogCreateRequest request) {
        if (!EmotionType.isValid(request.getEmotionId())) {
            throw new IllegalArgumentException("유효하지 않은 emotionId입니다.");
        }

        LocalDateTime selectedAt = request.getSelectedAt() != null
                ? request.getSelectedAt()
                : LocalDateTime.now();

        EmotionLog log = new EmotionLog(
                request.getRoomId(),
                loginUserPk,
                request.getEmotionId(),
                selectedAt
        );

        return emotionLogRepository.save(log);
    }

    @Transactional
    public EmotionLog save(Long loginUserPk, Long roomId, Integer emotionId) {
        if (!EmotionType.isValid(emotionId)) {
            throw new IllegalArgumentException("유효하지 않은 emotionId입니다.");
        }

        EmotionLog log = new EmotionLog(
                roomId,
                loginUserPk,
                emotionId,
                LocalDateTime.now()
        );

        return emotionLogRepository.save(log);
    }

    @Transactional
    public void deleteByUserIdAndRoomId(Long userId, Long roomId) {
        emotionLogRepository.deleteByUserIdAndRoomId(userId, roomId);
    }
}