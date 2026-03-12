package com.example.Yuhbaek.dto.catalog;

public record BookGenreResponse(
        String isbn,
        Integer rawCategoryId,
        String rawCategoryName,
        String genre
) {}