package com.example.Yuhbaek.dto.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinItemLookUpResponse(
        List<Item> item
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String title,
            String isbn13,
            Integer categoryId,
            String categoryName,
            String author,
            String publisher,
            String cover
    ) {}
}