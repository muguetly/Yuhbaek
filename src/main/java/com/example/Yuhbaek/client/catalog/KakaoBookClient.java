package com.example.Yuhbaek.client.catalog;

import com.example.Yuhbaek.dto.catalog.KakaoBookSearchApiResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

public class KakaoBookClient {

    private final RestClient restClient;

    public KakaoBookClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public KakaoBookSearchApiResponse search(String query, int page, int size) {
        String uri = UriComponentsBuilder.fromPath("/v3/search/book")
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(KakaoBookSearchApiResponse.class);
    }
}