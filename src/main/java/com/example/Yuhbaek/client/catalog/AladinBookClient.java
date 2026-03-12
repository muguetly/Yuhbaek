package com.example.Yuhbaek.client.catalog;

import com.example.Yuhbaek.dto.catalog.AladinItemLookUpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

public class AladinBookClient {

    private final RestClient restClient;
    private final String ttbKey;

    public AladinBookClient(RestClient restClient, String ttbKey) {
        this.restClient = restClient;
        this.ttbKey = ttbKey;
    }

    public AladinItemLookUpResponse lookupByIsbn13(String isbn13) {
        String uri = UriComponentsBuilder.fromPath("/ItemLookUp.aspx")
                .queryParam("ttbkey", ttbKey)
                .queryParam("itemIdType", "ISBN13")
                .queryParam("ItemId", isbn13)
                .queryParam("output", "js")
                .queryParam("Version", "20131101")
                .queryParam("OptResult", "categoryId")
                .build()
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(AladinItemLookUpResponse.class);
    }
}