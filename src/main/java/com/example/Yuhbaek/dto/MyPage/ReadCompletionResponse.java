package com.example.Yuhbaek.dto.MyPage;

import com.example.Yuhbaek.entity.MyPage.UserReadCompletion.CompletionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadCompletionResponse {

    private Long id;
    private String bookIsbn;
    private String bookTitle;
    private String bookAuthor;
    private String bookCover;
    private CompletionType completionType;
    private LocalDateTime completedAt;
}