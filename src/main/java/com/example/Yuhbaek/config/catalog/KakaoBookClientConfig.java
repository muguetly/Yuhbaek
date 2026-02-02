package com.example.Yuhbaek.config.catalog;

import com.example.Yuhbaek.client.catalog.KakaoBookClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(KakaoBookProperties.class)
public class KakaoBookClientConfig {

    @Bean
    RestClient kakaoRestClient(KakaoBookProperties props) {
        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeader("Authorization", "KakaoAK " + props.restApiKey())
                .build();
    }

    @Bean
    KakaoBookClient kakaoBookClient(RestClient kakaoRestClient) {
        return new KakaoBookClient(kakaoRestClient);
    }
}