package com.example.Yuhbaek.dto.Home;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizStatsResponse {

    private Integer totalQuizzes; // 총 푼 퀴즈 수
    private Integer correctCount; // 맞춘 개수
    private Double correctRate; // 정답률 (%)
    private Integer currentStreak; // 현재 연속 정답 일수
    private Boolean todayCompleted; // 오늘 퀴즈 완료 여부
}