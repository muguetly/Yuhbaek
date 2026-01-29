package com.example.Yuhbaek.dto.Home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchKeywordResponse {

    private String keyword;
    private Integer searchCount;
    private LocalDateTime lastSearchedAt;

    /**
     * 검색어 목록 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordListResponse {
        private List<SearchKeywordResponse> popularKeywords;  // 인기 검색어
        private List<SearchKeywordResponse> recentKeywords;   // 최신 검색어
    }
}