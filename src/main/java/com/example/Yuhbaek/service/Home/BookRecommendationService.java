package com.example.Yuhbaek.service.Home;

import com.example.Yuhbaek.dto.Home.BookRecommendationResponse;
import com.example.Yuhbaek.dto.Home.BookRecommendationResponse.RecommendedBook;
import com.example.Yuhbaek.dto.Home.BookRecommendationResponse.RecommendationMetadata;
import com.example.Yuhbaek.dto.Home.BookResponse;
import com.example.Yuhbaek.dto.Home.BookSearchRequest;
import com.example.Yuhbaek.dto.Home.BookSearchResponse;
import com.example.Yuhbaek.entity.SignUp.Genre;
import com.example.Yuhbaek.entity.SignUp.ReadingStyle;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.entity.SignUp.UserPreference;
import com.example.Yuhbaek.repository.SignUp.UserPreferenceRepository;
import com.example.Yuhbaek.repository.SignUp.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookRecommendationService {

    private final BookSearchService bookSearchService;
    private final UserRepository userRepository;
    private final UserPreferenceRepository preferenceRepository;

    // 장르별 검색 키워드 매핑
    private static final Map<String, List<String>> GENRE_KEYWORDS = Map.ofEntries(
            Map.entry("소설", Arrays.asList("소설", "문학", "이야기")),
            Map.entry("시/에세이", Arrays.asList("에세이", "시", "수필")),
            Map.entry("시_에세이", Arrays.asList("에세이", "시", "수필")),
            Map.entry("자기계발", Arrays.asList("자기계발", "성공", "동기부여", "자존감")),
            Map.entry("인문", Arrays.asList("인문", "철학", "역사", "사회")),
            Map.entry("경제/경영", Arrays.asList("경제", "경영", "투자", "재테크")),
            Map.entry("경제_경영", Arrays.asList("경제", "경영", "투자", "재테크")),
            Map.entry("과학", Arrays.asList("과학", "기술", "IT", "공학")),
            Map.entry("예술", Arrays.asList("예술", "미술", "음악", "디자인")),
            Map.entry("여행", Arrays.asList("여행", "관광", "여행기")),
            Map.entry("건강", Arrays.asList("건강", "의학", "운동", "다이어트")),
            Map.entry("요리", Arrays.asList("요리", "레시피", "음식"))
    );

    /**
     * 사용자 취향 기반 책 추천 (3권)
     */
    @Transactional(readOnly = true)
    public BookRecommendationResponse recommendBooks(Long userId) {
        try {
            // 1. 사용자 조회
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            // 2. 취향 정보 조회
            UserPreference preference = preferenceRepository.findByUser(user)
                    .orElse(null);

            // 3. 취향 정보가 없으면 인기 도서 추천
            if (preference == null || preference.getGenres().isEmpty()) {
                log.info("취향 정보 없음 - 인기 도서 추천: userId={}", userId);
                return recommendPopularBooks(userId);
            }

            // 4. 취향 기반 추천
            log.info("취향 기반 추천 시작: userId={}, genres={}", userId, preference.getGenres());
            return recommendByPreference(user, preference);

        } catch (Exception e) {
            log.error("책 추천 실패: userId={}, error={}", userId, e.getMessage(), e);
            return BookRecommendationResponse.builder()
                    .success(false)
                    .message("책 추천 중 오류가 발생했습니다: " + e.getMessage())
                    .recommendations(Collections.emptyList())
                    .build();
        }
    }

    /**
     * 취향 기반 추천
     */
    private BookRecommendationResponse recommendByPreference(UserEntity user, UserPreference preference) {
        List<RecommendedBook> allRecommendations = new ArrayList<>();

        // Genre Enum을 String으로 변환 (Enum.name() 사용)
        List<String> genreNames = preference.getGenres().stream()
                .map(Genre::name)  // Enum의 name() 메서드 사용
                .collect(Collectors.toList());

        // 각 장르별로 일정 개수씩 수집 (장르당 최대 7개)
        int booksPerGenre = Math.max(7, 20 / genreNames.size());

        for (String genre : genreNames) {
            List<String> keywords = GENRE_KEYWORDS.getOrDefault(genre, Arrays.asList(genre));
            int genreBookCount = 0;

            for (String keyword : keywords) {
                try {
                    // 카카오 API로 검색
                    BookSearchRequest searchRequest = BookSearchRequest.builder()
                            .query(keyword)
                            .target("title")
                            .page(1)
                            .size(10)
                            .sort("accuracy")
                            .build();

                    BookSearchResponse searchResponse = bookSearchService.searchBooks(searchRequest);

                    // 검색 결과를 추천 책으로 변환
                    List<RecommendedBook> genreBooks = searchResponse.getBooks().stream()
                            .map(book -> convertToRecommendedBook(book, genre, preference))
                            .collect(Collectors.toList());

                    allRecommendations.addAll(genreBooks);
                    genreBookCount += genreBooks.size();

                    // 이 장르에서 충분한 결과를 얻었으면 다음 장르로
                    if (genreBookCount >= booksPerGenre) {
                        break;
                    }

                } catch (Exception e) {
                    log.warn("장르 검색 실패: genre={}, keyword={}, error={}", genre, keyword, e.getMessage());
                }
            }
        }

        // 매칭 점수 기준으로 정렬하고 상위 3개 선택
        // 단, 가능하면 서로 다른 장르에서 1개씩 선택하도록 개선
        List<RecommendedBook> topRecommendations = selectDiverseRecommendations(allRecommendations, 3);

        // ReadingStyle Enum을 String으로 변환 (Enum.name() 사용)
        List<String> readingStyleNames = preference.getReadingStyles().stream()
                .map(ReadingStyle::name)  // Enum의 name() 메서드 사용
                .collect(Collectors.toList());

        // 메타데이터 생성
        RecommendationMetadata metadata = RecommendationMetadata.builder()
                .userId(user.getId())
                .basedOnGenres(genreNames)
                .basedOnStyles(readingStyleNames)
                .recommendationType("preference_based")
                .build();

        return BookRecommendationResponse.builder()
                .success(true)
                .message("취향 기반 추천이 완료되었습니다")
                .recommendations(topRecommendations)
                .metadata(metadata)
                .build();
    }

    /**
     * 다양한 장르의 책을 선택 (각 장르에서 최소 1개씩)
     */
    private List<RecommendedBook> selectDiverseRecommendations(List<RecommendedBook> allBooks, int count) {
        if (allBooks.isEmpty()) {
            return Collections.emptyList();
        }

        // 장르별로 그룹화
        Map<String, List<RecommendedBook>> booksByGenre = allBooks.stream()
                .collect(Collectors.groupingBy(RecommendedBook::getMatchReason));

        List<RecommendedBook> selected = new ArrayList<>();
        List<String> genres = new ArrayList<>(booksByGenre.keySet());

        // 1단계: 각 장르에서 최고 점수 책 1개씩 선택
        for (String genre : genres) {
            if (selected.size() >= count) break;

            List<RecommendedBook> genreBooks = booksByGenre.get(genre);
            RecommendedBook topBook = genreBooks.stream()
                    .max(Comparator.comparingDouble(RecommendedBook::getMatchScore))
                    .orElse(null);

            if (topBook != null && !selected.contains(topBook)) {
                selected.add(topBook);
            }
        }

        // 2단계: 부족하면 전체에서 점수 높은 순으로 추가
        if (selected.size() < count) {
            List<RecommendedBook> remaining = allBooks.stream()
                    .filter(book -> !selected.contains(book))
                    .sorted(Comparator.comparingDouble(RecommendedBook::getMatchScore).reversed())
                    .limit(count - selected.size())
                    .collect(Collectors.toList());

            selected.addAll(remaining);
        }

        // 최종적으로 점수순 정렬
        return selected.stream()
                .sorted(Comparator.comparingDouble(RecommendedBook::getMatchScore).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * 인기 도서 추천 (취향 정보 없을 때)
     */
    private BookRecommendationResponse recommendPopularBooks(Long userId) {
        List<RecommendedBook> recommendations = new ArrayList<>();

        // 인기 키워드로 검색
        String[] popularKeywords = {"베스트셀러", "추천", "화제의 책"};

        for (String keyword : popularKeywords) {
            try {
                BookSearchRequest searchRequest = BookSearchRequest.builder()
                        .query(keyword)
                        .target("title")
                        .page(1)
                        .size(5)
                        .sort("accuracy")
                        .build();

                BookSearchResponse searchResponse = bookSearchService.searchBooks(searchRequest);

                List<RecommendedBook> books = searchResponse.getBooks().stream()
                        .map(book -> RecommendedBook.builder()
                                .title(book.getTitle())
                                .authors(book.getAuthors())
                                .publisher(book.getPublisher())
                                .thumbnail(book.getThumbnailUrl())  // thumbnailUrl로 수정
                                .isbn(book.getIsbn())
                                .price(book.getPrice())
                                .description(book.getDescription())
                                .matchScore(70.0) // 기본 점수
                                .matchReason("인기 도서")
                                .build())
                        .collect(Collectors.toList());

                recommendations.addAll(books);

                if (recommendations.size() >= 3) {
                    break;
                }

            } catch (Exception e) {
                log.warn("인기 도서 검색 실패: keyword={}, error={}", keyword, e.getMessage());
            }
        }

        // 상위 3개 선택
        List<RecommendedBook> topRecommendations = recommendations.stream()
                .limit(3)
                .collect(Collectors.toList());

        RecommendationMetadata metadata = RecommendationMetadata.builder()
                .userId(userId)
                .basedOnGenres(Collections.emptyList())
                .basedOnStyles(Collections.emptyList())
                .recommendationType("popular")
                .build();

        return BookRecommendationResponse.builder()
                .success(true)
                .message("인기 도서를 추천합니다. 취향 설문을 작성하면 더 정확한 추천을 받을 수 있습니다.")
                .recommendations(topRecommendations)
                .metadata(metadata)
                .build();
    }

    /**
     * 책 정보를 추천 책으로 변환 (매칭 점수 계산)
     */
    private RecommendedBook convertToRecommendedBook(
            BookResponse book,
            String matchedGenre,
            UserPreference preference) {

        // 매칭 점수 계산
        double matchScore = calculateMatchScore(book, matchedGenre, preference);

        // ReadingStyle Enum을 String으로 변환
        List<String> styleNames = preference.getReadingStyles().stream()
                .map(ReadingStyle::name)  // Enum의 name() 메서드 사용
                .collect(Collectors.toList());

        // 추천 이유 생성
        String matchReason = generateMatchReason(matchedGenre, styleNames);

        return RecommendedBook.builder()
                .title(book.getTitle())
                .authors(book.getAuthors())
                .publisher(book.getPublisher())
                .thumbnail(book.getThumbnailUrl())  // thumbnailUrl로 수정
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .description(book.getDescription())
                .matchScore(matchScore)
                .matchReason(matchReason)
                .build();
    }

    /**
     * 매칭 점수 계산
     */
    private double calculateMatchScore(
            BookResponse book,
            String matchedGenre,
            UserPreference preference) {

        double score = 80.0; // 기본 점수

        // 제목이나 설명에 선호 장르 키워드가 있으면 가산점
        String bookInfo = (book.getTitle() + " " + book.getDescription()).toLowerCase();

        // Genre Enum의 이름 추출하여 비교
        for (Genre genre : preference.getGenres()) {
            if (bookInfo.contains(genre.name().toLowerCase())) {
                score += 5.0;
            }
        }

        // 독서 스타일 반영 - ReadingStyle Enum의 이름 추출
        for (ReadingStyle style : preference.getReadingStyles()) {
            String styleName = style.name();
            if (styleName.equals("빠른_전개") && bookInfo.contains("스릴")) {
                score += 3.0;
            } else if (styleName.equals("감동적인") && bookInfo.contains("감동")) {
                score += 3.0;
            } else if (styleName.equals("깊이_있는") && bookInfo.contains("철학")) {
                score += 3.0;
            }
        }

        // 최대 100점으로 제한
        return Math.min(score, 100.0);
    }

    /**
     * 추천 이유 생성
     */
    private String generateMatchReason(String genre, List<String> styles) {
        StringBuilder reason = new StringBuilder();

        // Enum 이름을 사용자 친화적으로 변환 (언더스코어를 슬래시나 공백으로)
        String displayGenre = genre.replace("_", "/");
        reason.append(displayGenre).append(" 장르를 좋아하시는 회원님께 추천합니다");

        if (!styles.isEmpty()) {
            String displayStyles = styles.stream()
                    .map(s -> s.replace("_", " "))
                    .collect(Collectors.joining(", "));
            reason.append(" (").append(displayStyles).append(" 스타일)");
        }

        return reason.toString();
    }
}