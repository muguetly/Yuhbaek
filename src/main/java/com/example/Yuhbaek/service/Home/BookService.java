package com.example.Yuhbaek.service.Home;

import com.example.Yuhbaek.config.Home.KakaoApiConfig;
import com.example.Yuhbaek.dto.Home.*;
import com.example.Yuhbaek.entity.Home.Book;
import com.example.Yuhbaek.repository.Home.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final WebClient webClient;
    private final KakaoApiConfig kakaoApiConfig;
    private final BookRepository bookRepository;

    /**
     * 카카오 API로 책 검색
     */
    public BookSearchResponse searchBooks(BookSearchRequest request) {
        try {
            // 카카오 API 호출
            KakaoBookSearchResponse kakaoResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("dapi.kakao.com")
                            .path("/v3/search/book")
                            .queryParam("query", request.getQuery())
                            .queryParam("target", request.getTarget())
                            .queryParam("page", request.getPage())
                            .queryParam("size", request.getSize())
                            .queryParam("sort", request.getSort())
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoApiConfig.getApiKey())
                    .retrieve()
                    .bodyToMono(KakaoBookSearchResponse.class)
                    .block();

            if (kakaoResponse == null || kakaoResponse.getDocuments() == null) {
                return BookSearchResponse.builder()
                        .books(new ArrayList<>())
                        .currentPage(request.getPage())
                        .pageSize(request.getSize())
                        .totalCount(0)
                        .isEnd(true)
                        .build();
            }

            // 카카오 응답을 BookResponse로 변환
            List<BookResponse> books = kakaoResponse.getDocuments().stream()
                    .map(this::convertToBookResponse)
                    .collect(Collectors.toList());

            return BookSearchResponse.builder()
                    .books(books)
                    .currentPage(request.getPage())
                    .pageSize(request.getSize())
                    .totalCount(kakaoResponse.getMeta().getTotalCount())
                    .isEnd(kakaoResponse.getMeta().getIsEnd())
                    .build();

        } catch (Exception e) {
            log.error("책 검색 실패: {}", e.getMessage(), e);
            throw new RuntimeException("책 검색 중 오류가 발생했습니다");
        }
    }

    /**
     * ISBN으로 책 상세 조회
     */
    @Transactional(readOnly = true)
    public BookResponse getBookByIsbn(String isbn) {
        // DB에서 먼저 조회
        return bookRepository.findByIsbn(isbn)
                .map(this::convertEntityToResponse)
                .orElseGet(() -> {
                    // DB에 없으면 카카오 API로 검색
                    BookSearchRequest searchRequest = BookSearchRequest.builder()
                            .query(isbn)
                            .target("isbn")
                            .size(1)
                            .build();

                    BookSearchResponse searchResponse = searchBooks(searchRequest);

                    if (searchResponse.getBooks().isEmpty()) {
                        throw new IllegalArgumentException("해당 ISBN의 책을 찾을 수 없습니다");
                    }

                    return searchResponse.getBooks().get(0);
                });
    }

    /**
     * 책 정보 DB에 저장
     */
    @Transactional
    public Book saveBook(KakaoBookSearchResponse.Document kakaoBook) {
        String isbn = extractIsbn(kakaoBook.getIsbn());

        // 이미 존재하는 경우 기존 데이터 반환
        if (bookRepository.existsByIsbn(isbn)) {
            return bookRepository.findByIsbn(isbn).get();
        }

        Book book = Book.builder()
                .isbn(isbn)
                .title(kakaoBook.getTitle())
                .authors(kakaoBook.getAuthors())
                .publisher(kakaoBook.getPublisher())
                .publishedDate(parseDate(kakaoBook.getDatetime()))
                .description(kakaoBook.getContents())
                .thumbnailUrl(kakaoBook.getThumbnail())
                .price(kakaoBook.getPrice())
                .salePrice(kakaoBook.getSalePrice())
                .url(kakaoBook.getUrl())
                .build();

        Book savedBook = bookRepository.save(book);
        log.info("책 정보 저장 완료 - ISBN: {}, 제목: {}", isbn, book.getTitle());

        return savedBook;
    }

    /**
     * 카카오 Document -> BookResponse 변환
     */
    private BookResponse convertToBookResponse(KakaoBookSearchResponse.Document doc) {
        return BookResponse.builder()
                .isbn(extractIsbn(doc.getIsbn()))
                .title(doc.getTitle())
                .authors(doc.getAuthors())
                .publisher(doc.getPublisher())
                .publishedDate(parseDate(doc.getDatetime()))
                .description(doc.getContents())
                .thumbnailUrl(doc.getThumbnail())
                .price(doc.getPrice())
                .salePrice(doc.getSalePrice())
                .url(doc.getUrl())
                .build();
    }

    /**
     * Book Entity -> BookResponse 변환
     */
    private BookResponse convertEntityToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .authors(book.getAuthors())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .description(book.getDescription())
                .thumbnailUrl(book.getThumbnailUrl())
                .price(book.getPrice())
                .salePrice(book.getSalePrice())
                .url(book.getUrl())
                .build();
    }

    /**
     * ISBN 추출 (ISBN13 우선, 없으면 ISBN10)
     */
    private String extractIsbn(String isbnString) {
        if (isbnString == null || isbnString.isBlank()) {
            return "";
        }

        String[] isbns = isbnString.split(" ");
        // ISBN13이 있으면 우선 사용 (보통 두 번째)
        return isbns.length > 1 ? isbns[1] : isbns[0];
    }

    /**
     * 날짜 문자열 파싱
     */
    private LocalDate parseDate(String dateString) {
        try {
            if (dateString == null || dateString.isBlank()) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateString);
            return null;
        }
    }
}
