package com.example.Yuhbaek.service.aichat;

import com.example.Yuhbaek.entity.aichat.AIChatRoom;
import com.example.Yuhbaek.entity.aichat.AIChatSession;
import com.example.Yuhbaek.entity.analytics.ThinkingStyleScore;
import com.example.Yuhbaek.entity.catalog.AllBook;
import com.example.Yuhbaek.entity.MyPage.UserReadCompletion.CompletionType;
import com.example.Yuhbaek.repository.aichat.AIChatMessageRepository;
import com.example.Yuhbaek.repository.aichat.AIChatRoomRepository;
import com.example.Yuhbaek.repository.aichat.AIChatSessionRepository;
import com.example.Yuhbaek.service.MyPage.ReadCompletionService;
import com.example.Yuhbaek.service.analytics.ThinkingStyleSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AIChatSessionService {

    private final AIChatSessionRepository sessionRepository;
    private final AIChatMessageRepository messageRepository;
    private final ThinkingStyleSnapshotService snapshotService;
    private final AIChatRoomService roomService;
    private final AIChatRoomRepository roomRepository;
    private final ReadCompletionService readCompletionService;

    @Transactional
    public Long startIfAbsent(Long userId, Long roomId) {
        return sessionRepository
                .findTopByUserIdAndRoomIdAndStatusOrderByIdDesc(userId, roomId, AIChatSession.Status.OPEN)
                .map(AIChatSession::getId)
                .orElseGet(() -> {
                    Long lastMsgId = messageRepository.findTopByRoomIdOrderByMessageIdDesc(roomId)
                            .map(m -> m.getMessageId())
                            .orElse(0L);

                    AIChatSession open = AIChatSession.open(userId, roomId, lastMsgId);
                    return sessionRepository.save(open).getId();
                });
    }

    @Transactional
    public ThinkingStyleScore end(Long userId, Long roomId) {
        AIChatSession session = sessionRepository
                .findTopByUserIdAndRoomIdAndStatusOrderByIdDesc(userId, roomId, AIChatSession.Status.OPEN)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 채팅 세션이 없습니다."));

        Long lastMsgId = messageRepository.findTopByRoomIdOrderByMessageIdDesc(roomId)
                .map(m -> m.getMessageId())
                .orElse(0L);

        session.close(lastMsgId, AIChatSession.EndType.STOP);
        sessionRepository.save(session);

        return snapshotService.analyzeSessionAndSave(
                userId, roomId, session.getStartMessageId(), session.getEndMessageId()
        );
    }

    @Transactional
    public ThinkingStyleScore finish(Long userId, Long roomId) {
        AIChatSession session = sessionRepository
                .findTopByUserIdAndRoomIdAndStatusOrderByIdDesc(userId, roomId, AIChatSession.Status.OPEN)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 채팅 세션이 없습니다."));

        Long lastMsgId = messageRepository.findTopByRoomIdOrderByMessageIdDesc(roomId)
                .map(m -> m.getMessageId())
                .orElse(0L);

        session.close(lastMsgId, AIChatSession.EndType.FINISH);
        sessionRepository.save(session);

        ThinkingStyleScore score = snapshotService.analyzeSessionAndSave(
                userId, roomId, session.getStartMessageId(), session.getEndMessageId()
        );

        roomService.finishRoom(userId, roomId);

        // 완독 처리
        AIChatRoom room = roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("room not found"));

        AllBook book = room.getBook();
        readCompletionService.markAsCompleted(
                userId,
                book.getIsbn(),
                book.getTitle(),
                book.getAuthorText(),
                book.getCoverUrl(),
                CompletionType.AI_CHAT
        );

        return score;
    }
}