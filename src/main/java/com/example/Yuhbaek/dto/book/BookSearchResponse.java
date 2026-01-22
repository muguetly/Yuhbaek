package com.example.Yuhbaek.dto.book;

import java.util.List;

public record BookSearchResponse(
        String query,
        int page,
        int size,
        boolean isEnd,
        int totalCount,
        List<BookSearchItem> items
) {}
