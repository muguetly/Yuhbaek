package com.example.Yuhbaek.config.Home;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AladinApiConfig {

    @Value("${aladin.api.key}")
    private String apiKey;

    @Value("${aladin.api.bestseller-url:http://www.aladin.co.kr/ttb/api/ItemList.aspx}")
    private String bestsellerUrl;
}