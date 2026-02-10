package com.example.Yuhbaek.controller.MyPage;

import com.example.Yuhbaek.dto.MyPage.*;
import com.example.Yuhbaek.service.MyPage.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "프로필 관리 API", description = "마이페이지 - 프로필 관련 API")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 🆕 프로필 조회 (취향 정보 포함)
     */
    @Operation(summary = "프로필 조회",
            description = "사용자의 프로필 정보와 취향 설문 결과를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping
    public ResponseEntity<?> getUserProfile(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        try {
            UserProfileResponse profile = userProfileService.getUserProfile(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", profile);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("프로필 조회 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 프로필 이미지 변경
     */
    @Operation(summary = "프로필 이미지 변경",
            description = "사용자의 프로필 이미지를 변경합니다 (S3 업로드)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfileImage(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Parameter(description = "프로필 이미지 파일", required = true)
            @RequestPart("profileImage") MultipartFile profileImage) {
        try {
            // 파일 유효성 검사
            if (profileImage.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "이미지 파일을 선택해주세요");
                return ResponseEntity.badRequest().body(result);
            }

            // 이미지 파일 형식 확인
            String contentType = profileImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "이미지 파일만 업로드 가능합니다");
                return ResponseEntity.badRequest().body(result);
            }

            String imageUrl = userProfileService.updateProfileImage(userId, profileImage);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "프로필 이미지가 변경되었습니다");
            result.put("imageUrl", imageUrl);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("프로필 이미지 변경 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("프로필 이미지 변경 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "프로필 이미지 변경 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 프로필 이미지 삭제
     */
    @Operation(summary = "프로필 이미지 삭제",
            description = "프로필 이미지를 삭제하고 기본 이미지로 변경합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 이미지 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/image")
    public ResponseEntity<?> deleteProfileImage(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        try {
            userProfileService.deleteProfileImage(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "프로필 이미지가 삭제되었습니다");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("프로필 이미지 삭제 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("프로필 이미지 삭제 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "프로필 이미지 삭제 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 닉네임 변경
     */
    @Operation(summary = "닉네임 변경",
            description = "사용자의 닉네임을 변경합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 닉네임"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/nickname")
    public ResponseEntity<?> updateNickname(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Valid @RequestBody NicknameUpdateRequest request) {
        try {
            userProfileService.updateNickname(userId, request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "닉네임이 변경되었습니다");
            result.put("nickname", request.getNickname());

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("닉네임 변경 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("닉네임 변경 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "닉네임 변경 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경",
            description = "사용자의 비밀번호를 변경합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Valid @RequestBody PasswordUpdateRequest request) {
        try {
            userProfileService.updatePassword(userId, request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "비밀번호가 변경되었습니다");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("비밀번호 변경 실패: {}", e.getMessage());

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "비밀번호 변경 중 오류가 발생했습니다");

            return ResponseEntity.status(500).body(result);
        }
    }
}