package com.example.Yuhbaek.service.aichat;

import com.example.Yuhbaek.dto.aichat.CreateRoomRequest;
import com.example.Yuhbaek.dto.aichat.RoomListItemResponse;
import com.example.Yuhbaek.entity.aichat.AIChatRoom;
import com.example.Yuhbaek.entity.aichat.RoomStatus;
import com.example.Yuhbaek.entity.catalog.AllBook;
import com.example.Yuhbaek.repository.aichat.AIChatMessageRepository;
import com.example.Yuhbaek.repository.aichat.AIChatRoomRepository;
import com.example.Yuhbaek.repository.aichat.AIChatSessionRepository;
import com.example.Yuhbaek.repository.analytics.EmotionLogRepository;
import com.example.Yuhbaek.repository.analytics.ThinkingStyleScoreRepository;
import com.example.Yuhbaek.repository.catalog.BookSerchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIChatRoomService {

    private final AIChatRoomRepository roomRepository;
    private final BookSerchRepository bookRepository;
    private final AIChatMessageRepository messageRepository;
    private final AIChatSessionRepository sessionRepository;
    private final EmotionLogRepository emotionLogRepository;
    private final ThinkingStyleScoreRepository thinkingStyleScoreRepository;

    @Transactional
    public Long createOrGetRoom(Long userId, CreateRoomRequest request) {
        AllBook book = bookRepository.findByIsbn(request.getIsbn())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다. 먼저 도서를 저장해주세요."));

        AIChatRoom room = new AIChatRoom();
        room.setUserId(userId);
        room.setBook(book);
        room.setStatus(RoomStatus.IN_PROGRESS);
        room.setRoomTitle(generateNextRoomTitle(userId, book.getId(), book.getTitle()));

        AIChatRoom saved = roomRepository.save(room);
        return saved.getRoomId();
    }

    @Transactional(readOnly = true)
    public List<RoomListItemResponse> listRooms(Long userId, RoomStatus status) {
        List<AIChatRoom> rooms = (status == null)
                ? roomRepository.findByUserIdOrderByLastMessageAtDescCreatedAtDesc(userId)
                : roomRepository.findByUserIdAndStatusOrderByLastMessageAtDescCreatedAtDesc(userId, status);

        return rooms.stream()
                .map(room -> RoomListItemResponse.builder()
                        .roomId(room.getRoomId())
                        .status(room.getStatus())
                        .lastMessageAt(room.getLastMessageAt())
                        .displayTime(room.getLastMessageAt() != null ? room.getLastMessageAt() : room.getCreatedAt())
                        .bookId(room.getBook().getId())
                        .title(room.getRoomTitle())
                        .coverUrl(room.getBook().getCoverUrl())
                        .genre(room.getBook().getGenre())
                        .build())
                .toList();
    }

    @Transactional
    public void discardRoom(Long userId, Long roomId) {
        AIChatRoom room = roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 없거나 권한이 없습니다."));

        messageRepository.deleteByRoomId(roomId);
        sessionRepository.deleteByUserIdAndRoomId(userId, roomId);
        emotionLogRepository.deleteByUserIdAndRoomId(userId, roomId);
        thinkingStyleScoreRepository.deleteByUserIdAndRoomId(userId, roomId);

        roomRepository.delete(room);
    }

    @Transactional
    public void finishRoom(Long userId, Long roomId) {
        AIChatRoom room = roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 없거나 권한이 없습니다."));

        room.setStatus(RoomStatus.FINISHED);

        if (room.getLastMessageAt() == null) {
            room.setLastMessageAt(room.getUpdatedAt() != null ? room.getUpdatedAt() : room.getCreatedAt());
        }

        roomRepository.save(room);
    }

    private String generateNextRoomTitle(Long userId, Long bookId, String baseTitle) {
        List<AIChatRoom> existingRooms =
                roomRepository.findByUserIdAndBook_IdOrderByCreatedAtAsc(userId, bookId);

        if (existingRooms.isEmpty()) {
            return baseTitle;
        }

        int maxNumber = 1;

        for (AIChatRoom room : existingRooms) {
            String title = room.getRoomTitle();

            if (title == null || title.isBlank()) {
                continue;
            }

            if (title.equals(baseTitle)) {
                maxNumber = Math.max(maxNumber, 1);
                continue;
            }

            String prefix = baseTitle + "(";
            if (title.startsWith(prefix) && title.endsWith(")")) {
                String numberPart = title.substring(prefix.length(), title.length() - 1);
                try {
                    int n = Integer.parseInt(numberPart);
                    maxNumber = Math.max(maxNumber, n);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return baseTitle + "(" + (maxNumber + 1) + ")";
    }
}