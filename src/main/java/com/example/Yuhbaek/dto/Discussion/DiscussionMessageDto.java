package com.example.Yuhbaek.dto.Discussion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "토론 메시지")
public class DiscussionMessageDto {

    @Schema(description = "메시지 ID", example = "1")
    private Long id;

    @Schema(description = "토론방 ID", example = "1")
    private Long discussionRoomId;

    @Schema(description = "메시지 타입", example = "CHAT")
    private String type;

    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;

    @Schema(description = "발신자 정보")
    private SenderInfo sender;

    @Schema(description = "전송 시간")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "발신자 정보")
    public static class SenderInfo {
        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "사용자 아이디", example = "user123")
        private String userId;

        @Schema(description = "닉네임", example = "독서왕")
        private String nickname;
    }
}