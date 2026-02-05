package com.example.Yuhbaek.dto.MyPage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "독서장 수정 요청")
public class ReadingNoteUpdateRequest {

    @NotBlank(message = "기억에 남는 문구는 필수입니다")
    @Schema(description = "수정할 기억에 남는 문구",
            example = "세상에서 가장 어려운 것은 자기 자신을 이해하는 것이다.")
    private String memorableQuote;
}