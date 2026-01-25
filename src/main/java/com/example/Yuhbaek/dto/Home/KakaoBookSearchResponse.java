package com.example.Yuhbaek.dto.Home;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 카카오 책 검색 API 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoBookSearchResponse {

    private Meta meta;
    private List<Document> documents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        @JsonProperty("total_count")
        private Integer totalCount;

        @JsonProperty("pageable_count")
        private Integer pageableCount;

        @JsonProperty("is_end")
        private Boolean isEnd;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private String title;
        private String contents;
        private String url;
        private String isbn;
        private String datetime;
        private List<String> authors;
        private String publisher;
        private List<String> translators;
        private Integer price;

        @JsonProperty("sale_price")
        private Integer salePrice;

        private String thumbnail;
        private String status;
    }
}
