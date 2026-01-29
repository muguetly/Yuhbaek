package com.example.Yuhbaek.service.aichat;

import com.example.Yuhbaek.dto.aichat.CreateRoomRequest;
import com.example.Yuhbaek.entity.aichat.AIChatRoom;
import com.example.Yuhbaek.repository.aichat.AIChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Yuhbaek.dto.aichat.RoomListItemResponse;
import com.example.Yuhbaek.entity.aichat.RoomStatus;
import com.example.Yuhbaek.entity.catalog.AllBook;
import com.example.Yuhbaek.repository.catalog.BookSerchRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class AIChatRoomService {

    private final AIChatRoomRepository roomRepository;
    private final BookSerchRepository bookRepository;

    @Transactional
    public Long createOrGetRoom(Long userId, CreateRoomRequest req) {

        if (req.getIsbn() == null || req.getIsbn().isBlank()) {
            throw new IllegalArgumentException("isbn is required");
        }
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new IllegalArgumentException("title is required");
        }

        // 1) 선택한 책만 upsert 저장
        AllBook book = bookRepository.findByIsbn(req.getIsbn())
                .orElseGet(AllBook::new);

        if (book.getId() == null) {
            book.setIsbn(req.getIsbn());
        }

        // 검색 결과 스냅샷 저장
        book.setTitle(req.getTitle());
        book.setCoverUrl(req.getCoverUrl());
        book.setGenre(req.getGenre());
        book.setAuthorText(req.getAuthorText());
        book.setPublisher(req.getPublisher());

        book = bookRepository.save(book);

        book = bookRepository.save(book);

        final AllBook finalBook = book;

        return roomRepository.findByUserIdAndBook_Id(userId, finalBook.getId())
                .map(AIChatRoom::getRoomId)
                .orElseGet(() -> {
                    AIChatRoom r = new AIChatRoom();
                    r.setUserId(userId);
                    r.setBook(finalBook);
                    return roomRepository.save(r).getRoomId();
                });

    }
    @Transactional(readOnly = true)
    public List<RoomListItemResponse> listRooms(Long userId, RoomStatus status) {

        var rooms = (status == null)
                ? roomRepository.findByUserIdOrderByLastMessageAtDescCreatedAtDesc(userId)
                : roomRepository.findByUserIdAndStatusOrderByLastMessageAtDescCreatedAtDesc(userId, status);

        return rooms.stream().map(r -> RoomListItemResponse.builder()
                .roomId(r.getRoomId())
                .status(r.getStatus())
                .lastMessageAt(r.getLastMessageAt())
                .bookId(r.getBook().getId())
                .title(r.getBook().getTitle())
                .coverUrl(r.getBook().getCoverUrl())
                .genre(r.getBook().getGenre())
                .build()
        ).toList();
    }
    @Transactional
    public void finishRoom(Long userId, Long roomId) {

        AIChatRoom room = roomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("room not found"));

        room.setStatus(RoomStatus.FINISHED);

        // (선택) 완독 처리한 순간도 정렬에 반영하고 싶으면
        if (room.getLastMessageAt() == null) {
            room.setLastMessageAt(java.time.LocalDateTime.now());
        }
    }

}