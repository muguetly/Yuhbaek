package com.example.Yuhbaek.service.Book;

import com.example.Yuhbaek.client.Book.KakaoBookClient;
import com.example.Yuhbaek.dto.Book.*;
import com.example.Yuhbaek.entity.Book.AllBook;
import com.example.Yuhbaek.repository.Book.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final KakaoBookClient kakaoBookClient;
    private final BookRepository bookRepository;

    public BookService(KakaoBookClient kakaoBookClient, BookRepository bookRepository) {
        this.kakaoBookClient = kakaoBookClient;
        this.bookRepository = bookRepository;
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
                        d.contents()
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
        AllBook book = bookRepository.findByIsbn(req.isbn())
                .orElseGet(AllBook::new);

        book.setIsbn(req.isbn());
        book.setTitle(req.title());
        book.setAuthorText(req.authors() == null ? null : String.join(", ", req.authors()));
        book.setPublisher(req.publisher());
        book.setCoverUrl(req.thumbnail());
        book.setGenre(req.genre());

        return bookRepository.save(book).getId();
    }

    /** 카카오 isbn: "ISBN10 ISBN13" 형태일 수 있어서 13자리 우선 */
    private String pickIsbn(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String[] parts = raw.trim().split("\\s+");
        // 13자리 우선
        for (String p : parts) {
            String digits = p.replaceAll("[^0-9Xx]", "");
            if (digits.length() == 13) return digits;
        }
        // 없으면 첫번째
        return parts[0].replaceAll("[^0-9Xx]", "");
    }
}
