package com.example.Yuhbaek.dto.SignUp;

import com.example.Yuhbaek.entity.SignUp.Genre;
import com.example.Yuhbaek.entity.SignUp.ReadingStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "취향 설문 요청")
public class PreferenceRequest {

    @Schema(description = "선호 장르 목록", example = "[\"ROMANCE\", \"THRILLER\", \"ESSAY\"]")
    @NotEmpty(message = "최소 1개 이상의 장르를 선택해주세요")
    private Set<Genre> genres;

    @Schema(description = "읽는 스타일 목록", example = "[\"LIGHT_READ\", \"EASY_READ\"]")
    @NotEmpty(message = "최소 1개 이상의 읽는 스타일을 선택해주세요")
    private Set<ReadingStyle> readingStyles;
}