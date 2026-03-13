package com.example.Yuhbaek.client.catalog;

import com.example.Yuhbaek.dto.catalog.AladinItemLookUpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class AladinBookClient {

    private final RestClient restClient;
    private final String ttbKey;
    private final ObjectMapper objectMapper;

    public AladinBookClient(RestClient restClient, String ttbKey, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.ttbKey = ttbKey;
        this.objectMapper = objectMapper;
    }

    public AladinItemLookUpResponse lookupByIsbn13(String isbn13) {
        String uri = UriComponentsBuilder.fromPath("/ItemLookUp.aspx")
                .queryParam("ttbkey", ttbKey)
                .queryParam("itemIdType", "ISBN13")
                .queryParam("ItemId", isbn13)
                .queryParam("output", "js")
                .queryParam("Version", "20131101")
                .build()
                .toUriString();

        log.info("[AladinBookClient] request uri={}", uri);

        String rawBody = restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);

        log.info("[AladinBookClient] raw response={}", rawBody);

        try {
            return objectMapper.readValue(rawBody, AladinItemLookUpResponse.class);
        } catch (Exception e) {
            log.error("[AladinBookClient] JSON 파싱 실패. rawBody={}", rawBody, e);
            throw new RuntimeException("알라딘 응답 파싱 실패", e);
        }
    }
}