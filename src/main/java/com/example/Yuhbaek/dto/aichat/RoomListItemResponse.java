package com.example.Yuhbaek.dto.aichat;

import com.example.Yuhbaek.entity.aichat.RoomStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoomListItemResponse {
    private Long roomId;
    private RoomStatus status;
    private LocalDateTime lastMessageAt;

    private Long bookId;
    private String title;
    private String coverUrl;
    private String genre;
}