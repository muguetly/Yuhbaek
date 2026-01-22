package com.example.Yuhbaek.dto.Book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoBookSearchApiResponse(
        List<Document> documents,
        Meta meta
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Document(
            String title,
            String contents,
            String url,
            String isbn,        // "ISBN10 ISBN13" 형태로 올 수 있음
            String datetime,
            List<String> authors,
            String publisher,
            List<String> translators,
            Integer price,
            Integer sale_price,
            String thumbnail,
            String status
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Meta(
            boolean is_end,
            int pageable_count,
            int total_count
    ) {}
}
