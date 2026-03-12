package com.example.Yuhbaek.config.catalog;

import com.example.Yuhbaek.client.catalog.AladinBookClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AladinBookProperties.class)
public class AladinBookClientConfig {

    @Bean
    RestClient aladinRestClient(AladinBookProperties props) {
        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .build();
    }

    @Bean
    AladinBookClient aladinBookClient(RestClient aladinRestClient, AladinBookProperties props) {
        return new AladinBookClient(aladinRestClient, props.ttbKey());
    }
}