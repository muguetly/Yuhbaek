package com.example.Yuhbaek.dto.catalog;

import java.util.List;

public record BookSearchItem(
        String title,
        String isbn,
        List<String> authors,
        String publisher,
        String thumbnail,
        String contents,
        String genre
) {}