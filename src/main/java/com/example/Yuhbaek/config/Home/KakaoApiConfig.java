package com.example.Yuhbaek.config.Home;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KakaoApiConfig {

    @Value("${kakao.api.key}")
    private String apiKey;

    @Value("${kakao.api.book-search-url:https://dapi.kakao.com/v3/search/book}")
    private String bookSearchUrl;
}
