package com.example.Yuhbaek.dto.SignUp;

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
@Schema(description = "취향 설문 응답")
public class PreferenceResponse {

    @Schema(description = "취향 설정 ID")
    private Long id;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "선호 장르 목록")
    private Set<Genre> genres;

    @Schema(description = "읽는 스타일 목록")
    private Set<ReadingStyle> readingStyles;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;
}