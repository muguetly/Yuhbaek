package com.example.Yuhbaek.service.MyPage;

import com.example.Yuhbaek.dto.MyPage.NicknameUpdateRequest;
import com.example.Yuhbaek.dto.MyPage.PasswordUpdateRequest;
import com.example.Yuhbaek.dto.MyPage.UserProfileResponse;  // 🆕 추가
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.entity.SignUp.UserPreference;  // 🆕 추가
import com.example.Yuhbaek.repository.SignUp.UserRepository;
import com.example.Yuhbaek.repository.SignUp.UserPreferenceRepository;  // 🆕 추가
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;  // 🆕 추가

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository preferenceRepository;  // 🆕 추가
    private final S3Service s3Service;  // ✅ 기존 그대로
    private final PasswordEncoder passwordEncoder;

    /**
     * 🆕 사용자 프로필 조회 (취향 정보 포함)
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 취향 정보 조회
        UserPreference preference = preferenceRepository.findByUserId(userId)
                .orElse(null);

        return UserProfileResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                // 취향 정보 추가 (없으면 빈 Set)
                .genres(preference != null ? preference.getGenres() : new HashSet<>())
                .readingStyles(preference != null ? preference.getReadingStyles() : new HashSet<>())
                .build();
    }

    /**
     * 프로필 이미지 변경
     */
    @Transactional
    public String updateProfileImage(Long userId, MultipartFile profileImage) {
        // ✅ 기존 코드 그대로
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            s3Service.deleteFile(user.getProfileImage());
        }

        String imageUrl = s3Service.uploadFile(profileImage, "profile");
        user.updateProfileImage(imageUrl);

        log.info("프로필 이미지 변경 - 사용자 ID: {}", userId);
        return imageUrl;
    }

    /**
     * 프로필 이미지 삭제 (기본 이미지로 변경)
     */
    @Transactional
    public void deleteProfileImage(Long userId) {
        // ✅ 기존 코드 그대로
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            s3Service.deleteFile(user.getProfileImage());
        }

        user.updateProfileImage(null);
        log.info("프로필 이미지 삭제 - 사용자 ID: {}", userId);
    }

    /**
     * 닉네임 변경
     */
    @Transactional
    public void updateNickname(Long userId, NicknameUpdateRequest request) {
        // ✅ 기존 코드 그대로
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
        }

        user.updateNickname(request.getNickname());
        log.info("닉네임 변경 - 사용자 ID: {}, 새 닉네임: {}", userId, request.getNickname());
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequest request) {
        // ✅ 기존 코드 그대로
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encodedPassword);

        log.info("비밀번호 변경 - 사용자 ID: {}", userId);
    }
}