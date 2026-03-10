package com.example.Yuhbaek.service.analytics;

import com.example.Yuhbaek.entity.analytics.ThinkingStyleScore;
import com.example.Yuhbaek.entity.aichat.AIChatMessage;
import com.example.Yuhbaek.entity.aichat.MessageRole;
import com.example.Yuhbaek.repository.analytics.ThinkingStyleScoreRepository;
import com.example.Yuhbaek.repository.aichat.AIChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThinkingStyleSnapshotService {

    private final AIChatMessageRepository messageRepository;
    private final ThinkingStyleScoreRepository scoreRepository;
    private final OpenAIThinkingStyleClient openAIClient;

    /**
     * ✅ "이번 세션 범위"만 분석해서 저장
     * 범위: (startMessageId, endMessageId]  (start는 제외, end는 포함)
     */
    public ThinkingStyleScore analyzeSessionAndSave(Long userId, Long roomId, Long startMessageId, Long endMessageId) {

        if (endMessageId == null || endMessageId <= (startMessageId == null ? 0L : startMessageId)) {
            // 메시지가 거의 없으면 저장 안 하거나, 기본값 저장 중 택1
            // 여기서는 예외 대신 "빈 분석"을 막는 용도로 return 처리
            throw new IllegalArgumentException("분석할 메시지가 없습니다.");
        }

        long start = (startMessageId == null ? 0L : startMessageId);

        List<AIChatMessage> messages =
                messageRepository.findByRoomIdAndMessageIdGreaterThanAndMessageIdLessThanEqualOrderByMessageIdAsc(
                        roomId, start, endMessageId
                );

        String text = messages.stream()
                .map(m -> (m.getRole() == MessageRole.USER ? "USER: " : "AI: ") + m.getContent())
                .collect(Collectors.joining("\n"));

        OpenAIThinkingStyleClient.Scores s = openAIClient.analyze(text);

        ThinkingStyleScore saved = new ThinkingStyleScore(
                userId, roomId,
                s.critic(), s.emotion(), s.analysis(), s.empathy(), s.creative(),
                LocalDateTime.now()
        );

        return scoreRepository.save(saved);
    }

    public ThinkingStyleScore analyzeAndSave(Long userId, Long roomId) {
        List<AIChatMessage> recent = messageRepository.findByRoomIdOrderByCreatedAtDesc(
                roomId, PageRequest.of(0, 30)
        );

        String text = recent.stream()
                .filter(m -> m.getRole() == MessageRole.USER)
                .map(AIChatMessage::getContent)
                .collect(Collectors.joining("\n"));

        OpenAIThinkingStyleClient.Scores s = openAIClient.analyze(text);

        ThinkingStyleScore saved = new ThinkingStyleScore(
                userId, roomId,
                s.critic(), s.emotion(), s.analysis(), s.empathy(), s.creative(),
                LocalDateTime.now()
        );
        return scoreRepository.save(saved);
    }
}
