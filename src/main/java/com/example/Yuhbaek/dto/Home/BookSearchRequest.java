package com.example.Yuhbaek.dto.Home;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "책 검색 요청")
public class BookSearchRequest {

    @Schema(description = "검색어", example = "미움받을 용기")
    private String query;

    @Schema(description = "검색 대상 (기본값: all)", example = "all",
            allowableValues = {"all", "title", "isbn", "publisher", "person"})
    @Builder.Default
    private String target = "all";

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
    @Builder.Default
    private Integer page = 1;

    @Schema(description = "한 페이지에 보여질 결과 수 (최대 50)", example = "10")
    @Builder.Default
    private Integer size = 10;

    @Schema(description = "정렬 방식", example = "accuracy",
            allowableValues = {"accuracy", "recency"})
    @Builder.Default
    private String sort = "accuracy";
}
