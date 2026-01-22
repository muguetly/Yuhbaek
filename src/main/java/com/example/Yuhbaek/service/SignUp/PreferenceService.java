package com.example.Yuhbaek.service.SignUp;

import com.example.Yuhbaek.dto.SignUp.PreferenceRequest;
import com.example.Yuhbaek.dto.SignUp.PreferenceResponse;
import com.example.Yuhbaek.entity.SignUp.UserPreference;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.repository.SignUp.UserPreferenceRepository;
import com.example.Yuhbaek.repository.SignUp.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    /**
     * 취향 설문 저장
     */
    @Transactional
    public PreferenceResponse savePreference(Long userId, PreferenceRequest request) {
        // 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 이미 설문을 완료했는지 확인
        if (user.isSurveyCompleted()) {
            throw new IllegalArgumentException("이미 취향 설문을 완료했습니다");
        }

        // 취향 정보 저장
        UserPreference preference = UserPreference.builder()
                .user(user)
                .genres(request.getGenres())
                .readingStyles(request.getReadingStyles())
                .build();

        UserPreference savedPreference = preferenceRepository.save(preference);

        // 사용자의 설문 완료 상태 업데이트
        user.setSurveyCompleted(true);
        userRepository.save(user);

        log.info("취향 설문 저장 완료 - 사용자: {}", user.getUserId());

        return convertToResponse(savedPreference);
    }

    /**
     * 취향 설문 수정
     */
    @Transactional
    public PreferenceResponse updatePreference(Long userId, PreferenceRequest request) {
        // 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 기존 취향 정보 조회
        UserPreference preference = preferenceRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("취향 정보를 찾을 수 없습니다"));

        // 취향 정보 수정
        preference.setGenres(request.getGenres());
        preference.setReadingStyles(request.getReadingStyles());

        UserPreference updatedPreference = preferenceRepository.save(preference);

        log.info("취향 설문 수정 완료 - 사용자: {}", user.getUserId());

        return convertToResponse(updatedPreference);
    }

    /**
     * 취향 설문 조회
     */
    @Transactional(readOnly = true)
    public PreferenceResponse getPreference(Long userId) {
        // 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 취향 정보 조회
        UserPreference preference = preferenceRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("취향 정보를 찾을 수 없습니다"));

        return convertToResponse(preference);
    }

    /**
     * Entity -> Response 변환
     */
    private PreferenceResponse convertToResponse(UserPreference preference) {
        return PreferenceResponse.builder()
                .id(preference.getId())
                .userId(preference.getUser().getUserId())
                .genres(preference.getGenres())
                .readingStyles(preference.getReadingStyles())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
}