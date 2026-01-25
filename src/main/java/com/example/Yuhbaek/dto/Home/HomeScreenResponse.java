package com.example.Yuhbaek.dto.Home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 홈 화면 전체 데이터 응답
 * - 책 검색 기능은 별도 API 사용
 * - 사용자 취향 기반 책 추천 (3개)
 * - 월간 베스트셀러 (예정)
 * - 오늘의 퀴즈 (예정)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeScreenResponse {

    private boolean success;
    private String message;
    private HomeData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeData {
        // 사용자 정보
        private UserInfo userInfo;

        // 취향 기반 추천 책 (3개)
        private List<BookRecommendationResponse.RecommendedBook> recommendedBooks;

        // 월간 베스트셀러 (추후 구현)
        private List<BestsellerBook> monthlyBestsellers;

        // 오늘의 퀴즈 (추후 구현)
        private DailyQuiz dailyQuiz;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private boolean surveyCompleted;
        private List<String> preferredGenres;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BestsellerBook {
        private Integer rank;
        private String title;
        private List<String> authors;
        private String thumbnail;
        private String isbn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyQuiz {
        private Long quizId;
        private String question;
        private List<String> options;
        private boolean completed;
    }
}