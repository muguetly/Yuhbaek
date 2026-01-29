package com.example.Yuhbaek.dto.Home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestsellerRequest {

    /**
     * 리스트 타입
     * Bestseller: 베스트셀러
     * ItemNewAll: 신간 전체
     * ItemNewSpecial: 주목할 만한 신간
     */
    @Builder.Default
    private String queryType = "Bestseller";

    /**
     * 검색 대상
     * Book: 국내도서
     * Foreign: 외국도서
     * Music: 음반
     * DVD: DVD
     * Used: 중고샵
     * eBook: 전자책
     */
    @Builder.Default
    private String searchTarget = "Book";

    /**
     * 조회 개수 (1-50, 기본값: 10)
     */
    @Builder.Default
    private Integer maxResults = 10;

    /**
     * 시작 페이지 (1부터 시작, 기본값: 1)
     */
    @Builder.Default
    private Integer start = 1;
}