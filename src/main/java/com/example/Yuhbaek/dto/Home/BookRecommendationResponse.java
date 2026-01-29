package com.example.Yuhbaek.dto.Home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRecommendationResponse {

    private boolean success;
    private String message;
    private List<RecommendedBook> recommendations;
    private RecommendationMetadata metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedBook {
        private String title;
        private List<String> authors;
        private String publisher;
        private String thumbnail;
        private String isbn;
        private Integer price;
        private String description;
        private Double matchScore;  // 취향 매칭 점수 (0-100)
        private String matchReason;  // 추천 이유
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationMetadata {
        private Long userId;
        private List<String> basedOnGenres;  // 추천 기반이 된 장르들
        private List<String> basedOnStyles;  // 추천 기반이 된 독서 스타일들
        private String recommendationType;   // "preference_based", "popular" 등
    }
}
