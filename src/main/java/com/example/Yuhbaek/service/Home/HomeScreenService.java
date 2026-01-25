package com.example.Yuhbaek.service.Home;

import com.example.Yuhbaek.dto.Home.BookRecommendationResponse;
import com.example.Yuhbaek.dto.Home.HomeScreenResponse;
import com.example.Yuhbaek.dto.Home.HomeScreenResponse.HomeData;
import com.example.Yuhbaek.dto.Home.HomeScreenResponse.UserInfo;
import com.example.Yuhbaek.entity.SignUp.Genre;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.entity.SignUp.UserPreference;
import com.example.Yuhbaek.repository.SignUp.UserPreferenceRepository;
import com.example.Yuhbaek.repository.SignUp.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeScreenService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final BookRecommendationService recommendationService;

    /**
     * 홈 화면 데이터 조회
     */
    @Transactional(readOnly = true)
    public HomeScreenResponse getHomeScreen(Long userId) {
        try {
            // 1. 사용자 조회
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            // 2. 사용자 정보 생성
            UserInfo userInfo = buildUserInfo(user);

            // 3. 취향 기반 책 추천 조회
            BookRecommendationResponse recommendationResponse = recommendationService.recommendBooks(userId);
            List<BookRecommendationResponse.RecommendedBook> recommendedBooks =
                    recommendationResponse.isSuccess() ?
                            recommendationResponse.getRecommendations() :
                            Collections.emptyList();

            // 4. 홈 데이터 구성
            HomeData homeData = HomeData.builder()
                    .userInfo(userInfo)
                    .recommendedBooks(recommendedBooks)
                    .monthlyBestsellers(Collections.emptyList())  // 추후 구현
                    .dailyQuiz(null)  // 추후 구현
                    .build();

            log.info("홈 화면 데이터 조회 완료: userId={}", userId);

            return HomeScreenResponse.builder()
                    .success(true)
                    .message("홈 화면 데이터를 성공적으로 불러왔습니다")
                    .data(homeData)
                    .build();

        } catch (Exception e) {
            log.error("홈 화면 데이터 조회 실패: userId={}, error={}", userId, e.getMessage(), e);

            return HomeScreenResponse.builder()
                    .success(false)
                    .message("홈 화면 데이터 조회 중 오류가 발생했습니다: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * 사용자 정보 구성
     */
    private UserInfo buildUserInfo(UserEntity user) {
        UserPreference preference = preferenceRepository.findByUser(user).orElse(null);

        // Genre Enum을 String으로 변환 (Enum.name() 사용)
        List<String> preferredGenres = Collections.emptyList();
        if (preference != null && preference.getGenres() != null) {
            preferredGenres = preference.getGenres().stream()
                    .map(Genre::name)  // Enum의 name() 메서드 사용
                    .collect(Collectors.toList());
        }

        return UserInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .surveyCompleted(user.isSurveyCompleted())
                .preferredGenres(preferredGenres)
                .build();
    }
}