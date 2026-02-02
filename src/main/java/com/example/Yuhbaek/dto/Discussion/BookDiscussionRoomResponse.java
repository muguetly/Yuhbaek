package com.example.Yuhbaek.dto.Discussion;

import com.example.Yuhbaek.entity.Discussion.BookDiscussionRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "도서 토론방 정보 응답")
public class BookDiscussionRoomResponse {

    @Schema(description = "토론방 ID", example = "1")
    private Long id;

    @Schema(description = "도서 제목", example = "1984")
    private String bookTitle;

    @Schema(description = "저자", example = "조지 오웰")
    private String bookAuthor;

    @Schema(description = "도서 ISBN", example = "9788932917245")
    private String bookIsbn;

    @Schema(description = "도서 표지 이미지 URL")
    private String bookCover;

    @Schema(description = "출판사", example = "민음사")
    private String bookPublisher;

    @Schema(description = "토론방 한줄소개", example = "디스토피아 소설에 대해 이야기해요")
    private String description;

    @Schema(description = "최대 참여 인원", example = "4")
    private Integer maxParticipants;

    @Schema(description = "현재 참여 인원", example = "2")
    private Integer currentParticipants;

    @Schema(description = "토론 시작 시간", example = "2025-01-28T19:00:00")
    private LocalDateTime discussionStartTime;

    @Schema(description = "토론방 상태", example = "WAITING")
    private String status;

    // ✅ 새로 추가: 대화 규칙/스타일
    @Schema(description = "대화 분위기/규칙/스타일", example = "[\"존댓말 사용\", \"비판 금지\", \"자유로운 분위기\"]")
    private List<String> discussionRules;

    @Schema(description = "방장 정보")
    private HostInfo host;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "방장 정보")
    public static class HostInfo {
        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "사용자 아이디", example = "user123")
        private String userId;

        @Schema(description = "닉네임", example = "독서왕")
        private String nickname;
    }

    /**
     * 참여 가능 여부
     */
    @Schema(description = "참여 가능 여부", example = "true")
    public boolean isAvailable() {
        return currentParticipants < maxParticipants &&
                !status.equals(BookDiscussionRoom.DiscussionStatus.FINISHED.name());
    }

    /**
     * 시작 여부
     */
    @Schema(description = "시작 여부", example = "false")
    public boolean isStarted() {
        return status.equals(BookDiscussionRoom.DiscussionStatus.IN_PROGRESS.name());
    }
}