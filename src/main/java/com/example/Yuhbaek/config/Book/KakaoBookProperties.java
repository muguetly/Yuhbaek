package com.example.Yuhbaek.config.Book;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.book")
public record KakaoBookProperties(
        String restApiKey,
        String baseUrl
) {}
