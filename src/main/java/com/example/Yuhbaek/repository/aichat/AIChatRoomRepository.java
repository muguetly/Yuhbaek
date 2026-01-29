package com.example.Yuhbaek.repository.aichat;

import com.example.Yuhbaek.entity.aichat.AIChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Yuhbaek.entity.aichat.RoomStatus;
import java.util.List;

import java.util.Optional;

public interface AIChatRoomRepository extends JpaRepository<AIChatRoom, Long> {


    List<AIChatRoom> findByUserIdOrderByLastMessageAtDescCreatedAtDesc(Long userId);

    List<AIChatRoom> findByUserIdAndStatusOrderByLastMessageAtDescCreatedAtDesc(Long userId, RoomStatus status);

    Optional<AIChatRoom> findByUserIdAndBook_Id(Long userId, Long bookId);

    Optional<AIChatRoom> findByRoomIdAndUserId(Long roomId, Long userId);
}