package com.example.Yuhbaek.repository.aichat;

import com.example.Yuhbaek.entity.aichat.AIChatRoom;
import com.example.Yuhbaek.entity.aichat.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AIChatRoomRepository extends JpaRepository<AIChatRoom, Long> {

    Optional<AIChatRoom> findByRoomIdAndUserId(Long roomId, Long userId);

    List<AIChatRoom> findByUserIdOrderByLastMessageAtDescCreatedAtDesc(Long userId);

    List<AIChatRoom> findByUserIdAndStatusOrderByLastMessageAtDescCreatedAtDesc(Long userId, RoomStatus status);

    List<AIChatRoom> findByUserIdAndBook_IdOrderByCreatedAtAsc(Long userId, Long bookId);

    boolean existsByUserIdAndBook_IdAndStatusAndRoomIdNot(
            Long userId,
            Long bookId,
            RoomStatus status,
            Long roomId
    );
}