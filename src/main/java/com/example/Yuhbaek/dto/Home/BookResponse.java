package com.example.Yuhbaek.dto.Home;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "책 정보")
public class BookResponse {

    @Schema(description = "책 ID (DB)")
    private Long id;

    @Schema(description = "ISBN")
    private String isbn;

    @Schema(description = "책 제목")
    private String title;

    @Schema(description = "저자 목록")
    private List<String> authors;

    @Schema(description = "출판사")
    private String publisher;

    @Schema(description = "출판일")
    private LocalDate publishedDate;

    @Schema(description = "책 소개")
    private String description;

    @Schema(description = "표지 이미지 URL")
    private String thumbnailUrl;

    @Schema(description = "정가")
    private Integer price;

    @Schema(description = "판매가")
    private Integer salePrice;

    @Schema(description = "상세 페이지 URL")
    private String url;
}
