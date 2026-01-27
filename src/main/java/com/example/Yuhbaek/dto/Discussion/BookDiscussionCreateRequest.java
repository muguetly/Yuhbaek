package com.example.Yuhbaek.dto.Discussion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "도서 토론방 생성 요청")
public class BookDiscussionCreateRequest {

    @Schema(description = "선택한 도서 제목", example = "1984")
    @NotBlank(message = "도서 제목은 필수입니다")
    @Size(max = 500, message = "도서 제목은 500자 이하여야 합니다")
    private String bookTitle;

    @Schema(description = "선택한 도서 저자", example = "조지 오웰")
    @NotBlank(message = "저자는 필수입니다")
    @Size(max = 500, message = "저자는 500자 이하여야 합니다")
    private String bookAuthor;

    @Schema(description = "도서 ISBN", example = "9788932917245")
    @Size(max = 100, message = "ISBN은 100자 이하여야 합니다")
    private String bookIsbn;

    @Schema(description = "도서 표지 이미지 URL", example = "https://image.aladin.co.kr/...")
    @Size(max = 1000, message = "표지 URL은 1000자 이하여야 합니다")
    private String bookCover;

    @Schema(description = "출판사", example = "민음사")
    @Size(max = 500, message = "출판사는 500자 이하여야 합니다")
    private String bookPublisher;

    @Schema(description = "토론방 한줄소개", example = "디스토피아 소설에 대해 이야기해요")
    @Size(max = 500, message = "한줄소개는 500자 이하여야 합니다")
    private String description;

    @Schema(description = "최대 참여 인원 (기본 4명)", example = "4")
    @Min(value = 2, message = "최소 2명 이상이어야 합니다")
    @Max(value = 4, message = "최대 4명까지 가능합니다")
    private Integer maxParticipants = 4;

    @Schema(description = "토론 시작 시간", example = "2025-01-28T19:00:00")
    @NotNull(message = "토론 시작 시간은 필수입니다")
    @Future(message = "토론 시작 시간은 현재 시간 이후여야 합니다")
    private LocalDateTime discussionStartTime;

    @Schema(description = "방장 사용자 ID (시스템에서 자동 설정)")
    private Long hostId;
}