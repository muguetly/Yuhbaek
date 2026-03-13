package com.example.Yuhbaek.service.catalog;

import com.example.Yuhbaek.client.catalog.AladinBookClient;
import com.example.Yuhbaek.dto.catalog.AladinItemLookUpResponse;
import com.example.Yuhbaek.dto.catalog.BookGenreResponse;
import com.example.Yuhbaek.util.GenreNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookGenreService {

    private final AladinBookClient aladinBookClient;

    public BookGenreResponse getGenreByIsbn(String isbn13) {
        try {
            AladinItemLookUpResponse response = aladinBookClient.lookupByIsbn13(isbn13);

            log.info("[BookGenreService] isbn={}, response={}", isbn13, response);

            if (response == null || response.item() == null || response.item().isEmpty()) {
                log.warn("[BookGenreService] 알라딘 응답 비어있음. isbn={}", isbn13);
                return new BookGenreResponse(isbn13, null, null, "기타");
            }

            AladinItemLookUpResponse.Item item = response.item().get(0);

            log.info(
                    "[BookGenreService] isbn={}, categoryId={}, categoryName={}",
                    isbn13, item.categoryId(), item.categoryName()
            );

            String normalizedGenre = GenreNormalizer.normalizeFromAladin(item.categoryName());

            return new BookGenreResponse(
                    item.isbn13(),
                    item.categoryId(),
                    item.categoryName(),
                    normalizedGenre
            );
        } catch (Exception e) {
            log.error("[BookGenreService] 알라딘 장르 조회 실패. isbn={}", isbn13, e);
            return new BookGenreResponse(isbn13, null, null, "기타");
        }
    }
}