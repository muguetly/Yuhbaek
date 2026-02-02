package com.example.Yuhbaek.dto.catalog;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record BookSaveRequest(
        @NotBlank String isbn,
        @NotBlank String title,
        List<String> authors,
        String publisher,
        String thumbnail,
        String genre
) {}