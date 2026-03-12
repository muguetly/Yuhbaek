package com.example.Yuhbaek.service.catalog;

import com.example.Yuhbaek.client.catalog.AladinBookClient;
import com.example.Yuhbaek.dto.catalog.AladinItemLookUpResponse;
import com.example.Yuhbaek.dto.catalog.BookGenreResponse;
import com.example.Yuhbaek.util.GenreNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookGenreService {

    private final AladinBookClient aladinBookClient;

    public BookGenreResponse getGenreByIsbn(String isbn13) {
        AladinItemLookUpResponse response = aladinBookClient.lookupByIsbn13(isbn13);

        if (response == null || response.item() == null || response.item().isEmpty()) {
            return new BookGenreResponse(isbn13, null, null, "기타");
        }

        AladinItemLookUpResponse.Item item = response.item().get(0);
        String normalizedGenre = GenreNormalizer.normalizeFromAladin(item.categoryName());

        return new BookGenreResponse(
                item.isbn13(),
                item.categoryId(),
                item.categoryName(),
                normalizedGenre
        );
    }
}