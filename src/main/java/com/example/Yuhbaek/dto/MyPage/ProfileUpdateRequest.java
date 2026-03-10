package com.example.Yuhbaek.dto.MyPage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로필 이미지 변경 요청")
public class ProfileUpdateRequest {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
}