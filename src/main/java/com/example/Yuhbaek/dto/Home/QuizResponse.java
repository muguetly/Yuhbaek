package com.example.Yuhbaek.dto.Home;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponse {

    private Long id;
    private String question;
    private String category;

    // 답변 후에만 포함되는 정보
    private Boolean answer; // 정답
    private String explanation; // 해설
    private Boolean userAnswer; // 사용자 답변
    private Boolean isCorrect; // 정답 여부
}