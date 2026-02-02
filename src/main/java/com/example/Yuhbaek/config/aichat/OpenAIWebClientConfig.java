package com.example.Yuhbaek.config.aichat;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAIWebClientConfig {

    @Bean
    @Qualifier("openaiWebClient")
    public WebClient openaiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com")
                .build();
    }
}