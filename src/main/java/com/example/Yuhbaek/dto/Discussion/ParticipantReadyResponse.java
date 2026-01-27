package com.example.Yuhbaek.dto.Discussion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "참여자 준비 상태 응답")
public class ParticipantReadyResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "닉네임", example = "독서왕")
    private String nickname;

    @Schema(description = "준비 완료 여부", example = "true")
    private Boolean isReady;

    @Schema(description = "모든 참여자 준비 완료 여부", example = "false")
    private Boolean allReady;

    @Schema(description = "토론방 상태", example = "WAITING")
    private String roomStatus;
}