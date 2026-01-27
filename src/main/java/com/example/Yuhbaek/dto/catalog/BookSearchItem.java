package com.example.Yuhbaek.dto.catalog;

import java.util.List;

public record BookSearchItem(
        String title,
        String isbn,          // 우리가 쓸 ISBN(가능하면 13)
        List<String> authors,
        String publisher,
        String thumbnail,
        String contents
) {}