package com.example.Yuhbaek.dto.catalog;

import java.util.List;

public record BookSearchResponse(
        String query,
        int page,
        int size,
        boolean isEnd,
        int totalCount,
        List<BookSearchItem> items
) {}