package com.example.Yuhbaek.dto.Discussion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "참여자 정보")
public class ParticipantDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "닉네임", example = "독서왕")
    private String nickname;

    @Schema(description = "역할", example = "HOST")
    private String role;

    @Schema(description = "준비 완료 여부", example = "true")
    private Boolean isReady;

    @Schema(description = "입장 시간")
    private LocalDateTime joinedAt;
}