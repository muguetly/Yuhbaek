package com.example.Yuhbaek.dto.MyPage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "책 찜 추가 요청")
public class WishlistAddRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @NotBlank(message = "책 ISBN은 필수입니다")
    @Schema(description = "책 ISBN", example = "9788936433598")
    private String bookIsbn;

    @NotBlank(message = "책 제목은 필수입니다")
    @Schema(description = "책 제목", example = "1Q84")
    private String bookTitle;

    @Schema(description = "저자", example = "무라카미 하루키")
    private String author;

    @Schema(description = "표지 이미지 URL")
    private String coverImage;

    @Schema(description = "출판사", example = "문학동네")
    private String publisher;
}