package com.example.Yuhbaek.dto.MyPage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "독서장 응답")
public class ReadingNoteResponse {

    @Schema(description = "독서장 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "책 ISBN", example = "9788936433598")
    private String bookIsbn;

    @Schema(description = "책 제목", example = "1Q84")
    private String bookTitle;

    @Schema(description = "저자", example = "무라카미 하루키")
    private String author;

    @Schema(description = "표지 이미지 URL")
    private String coverImage;

    @Schema(description = "출판사", example = "문학동네")
    private String publisher;

    @Schema(description = "기억에 남는 문구")
    private String memorableQuote;

    @Schema(description = "작성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;
}