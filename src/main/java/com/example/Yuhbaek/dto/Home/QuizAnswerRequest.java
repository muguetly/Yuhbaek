package com.example.Yuhbaek.dto.Home;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswerRequest {

    @NotNull(message = "퀴즈 ID는 필수입니다")
    private Long quizId;

    @NotNull(message = "답변은 필수입니다")
    private Boolean answer; // true = O, false = X
}