package com.example.Yuhbaek.config.catalog;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aladin.book")
public record AladinBookProperties(
        String ttbKey,
        String baseUrl
) {}