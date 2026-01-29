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
public class BestsellerResponse {

    // 메타 정보
    private String title;        // 리스트 제목
    private Integer totalResults; // 전체 결과 수
    private Integer startIndex;   // 시작 인덱스
    private Integer itemsPerPage; // 페이지당 항목 수
    private LocalDateTime pubDate; // 발행일

    // 베스트셀러 목록
    private List<BestsellerItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BestsellerItem {
        private String title;           // 책 제목
        private String author;          // 저자
        private String publisher;       // 출판사
        private String pubDate;         // 출간일
        private String description;     // 책 소개
        private String isbn;            // ISBN
        private String isbn13;          // ISBN13
        private Integer priceStandard;  // 정가
        private Integer priceSales;     // 판매가
        private String cover;           // 표지 이미지 URL
        private Integer categoryId;     // 카테고리 ID
        private String categoryName;    // 카테고리명
        private String link;            // 알라딘 상품 링크
        private Integer rank;           // 베스트셀러 순위 (추가)
    }
}