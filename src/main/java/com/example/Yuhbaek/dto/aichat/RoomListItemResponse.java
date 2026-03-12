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
    /**
     * 채팅방 목록 오른쪽 시간 표시용 포맷 함수
     *
     * 규칙:
     * - 오늘: "오후 3:21" 처럼 시간 표시
     * - 어제: "어제"
     * - 2~6일 전: "3일 전"
     * - 7일 이상: "3/6"
     */
    private LocalDateTime lastMessageAt;
    private LocalDateTime displayTime;
    private Long bookId;
    private String title;
    private String coverUrl;
    private String genre;
}