package com.example.Yuhbaek.dto.MyPage;

import com.example.Yuhbaek.entity.SignUp.Genre;
import com.example.Yuhbaek.entity.SignUp.ReadingStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 프로필 정보")
public class UserProfileResponse {

    @Schema(description = "사용자 ID")
    private Long id;

    @Schema(description = "아이디")
    private String userId;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "프로필 이미지 URL")
    private String profileImage;

    @Schema(description = "가입일")
    private LocalDateTime createdAt;

    // 🆕 취향 정보 추가
    @Schema(description = "선호 장르 목록")
    private Set<Genre> genres;

    @Schema(description = "읽는 스타일 목록")
    private Set<ReadingStyle> readingStyles;
}
