package com.example.Yuhbaek.config.catalog;

import com.example.Yuhbaek.client.catalog.AladinBookClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AladinBookProperties.class)
public class AladinBookClientConfig {

    @Bean
    RestClient aladinRestClient(AladinBookProperties props) {
        String rawUrl = props.bestsellerUrl();

        String apiBaseUrl;
        if (rawUrl.endsWith("/ItemList.aspx")) {
            apiBaseUrl = rawUrl.substring(0, rawUrl.length() - "/ItemList.aspx".length());
        } else {
            apiBaseUrl = rawUrl;
        }

        return RestClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
    }

    @Bean
    AladinBookClient aladinBookClient(
            RestClient aladinRestClient,
            AladinBookProperties props,
            ObjectMapper objectMapper
    ) {
        return new AladinBookClient(aladinRestClient, props.key(), objectMapper);
    }
}