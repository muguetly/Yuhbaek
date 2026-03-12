package com.example.Yuhbaek.service.catalog;

import com.example.Yuhbaek.client.catalog.KakaoBookClient;
import com.example.Yuhbaek.dto.catalog.BookSaveRequest;
import com.example.Yuhbaek.dto.catalog.BookSearchItem;
import com.example.Yuhbaek.dto.catalog.BookSearchResponse;
import com.example.Yuhbaek.dto.catalog.KakaoBookSearchApiResponse;
import com.example.Yuhbaek.entity.catalog.AllBook;
import com.example.Yuhbaek.repository.catalog.BookSerchRepository;
import com.example.Yuhbaek.util.GenreNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final KakaoBookClient kakaoBookClient;
    private final BookSerchRepository bookSerchRepository;

    public CatalogService(KakaoBookClient kakaoBookClient, BookSerchRepository bookSerchRepository) {
        this.kakaoBookClient = kakaoBookClient;
        this.bookSerchRepository = bookSerchRepository;
    }

    public BookSearchResponse search(String query, int page, int size) {
        KakaoBookSearchApiResponse res = kakaoBookClient.search(query, page, size);
        if (res == null) {
            return new BookSearchResponse(query, page, size, true, 0, List.of());
        }

        List<BookSearchItem> items = res.documents().stream()
                .filter(Objects::nonNull)
                .map(d -> new BookSearchItem(
                        d.title(),
                        pickIsbn(d.isbn()),
                        d.authors(),
                        d.publisher(),
                        d.thumbnail(),
                        d.contents(),
                        "기타"
                ))
                .collect(Collectors.toList());

        return new BookSearchResponse(
                query,
                page,
                size,
                res.meta() != null && res.meta().is_end(),
                res.meta() != null ? res.meta().total_count() : 0,
                items
        );
    }

    @Transactional
    public Long saveOrUpdate(BookSaveRequest req) {
        AllBook book = bookSerchRepository.findByIsbn(req.isbn())
                .orElseGet(AllBook::new);

        book.setIsbn(req.isbn());
        book.setTitle(req.title());
        book.setAuthorText(req.authors() == null ? null : String.join(", ", req.authors()));
        book.setPublisher(req.publisher());
        book.setCoverUrl(req.thumbnail());
        book.setGenre(GenreNormalizer.normalizeFromClient(req.genre()));

        return bookSerchRepository.save(book).getId();
    }

    private String pickIsbn(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String[] parts = raw.trim().split("\\s+");
        for (String p : parts) {
            String digits = p.replaceAll("[^0-9Xx]", "");
            if (digits.length() == 13) return digits;
        }
        return parts[0].replaceAll("[^0-9Xx]", "");
    }
}