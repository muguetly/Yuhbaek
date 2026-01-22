package com.example.Yuhbaek.dto.Book;

import java.util.List;

public record BookSearchResponse(
        String query,
        int page,
        int size,
        boolean isEnd,
        int totalCount,
        List<BookSearchItem> items
) {}
