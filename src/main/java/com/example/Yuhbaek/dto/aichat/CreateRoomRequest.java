package com.example.Yuhbaek.dto.aichat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRoomRequest {

    @NotBlank
    private String isbn;

    @NotBlank
    private String title;

    private String coverUrl;
    private String genre;
    private String authorText;
    private String publisher;

    @NotNull
    private Integer emotionId;
}