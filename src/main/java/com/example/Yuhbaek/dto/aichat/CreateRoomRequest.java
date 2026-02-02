package com.example.Yuhbaek.dto.aichat;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter
@NoArgsConstructor

public class CreateRoomRequest {
    private String isbn;      // 필수
    private String title;     // 필수
    private String coverUrl;  // 선택
    private String genre;     // 선택
    private String authorText; // 선택
    private String publisher;  // 선택
}