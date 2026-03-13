package com.example.Yuhbaek.config.catalog;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aladin.api")
public record AladinBookProperties(
        String key,
        String bestsellerUrl
) {}