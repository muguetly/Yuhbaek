package com.example.Yuhbaek.dto.Home;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "책 검색 결과")
public class BookSearchResponse {

    @Schema(description = "검색된 책 목록")
    private List<BookResponse> books;

    @Schema(description = "현재 페이지")
    private Integer currentPage;

    @Schema(description = "페이지당 결과 수")
    private Integer pageSize;

    @Schema(description = "총 결과 수")
    private Integer totalCount;

    @Schema(description = "마지막 페이지 여부")
    private Boolean isEnd;
}
