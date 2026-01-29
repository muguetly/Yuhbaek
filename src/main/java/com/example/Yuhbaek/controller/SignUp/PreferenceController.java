package com.example.Yuhbaek.controller.SignUp;

import com.example.Yuhbaek.dto.SignUp.PreferenceRequest;
import com.example.Yuhbaek.dto.SignUp.PreferenceResponse;
import com.example.Yuhbaek.service.SignUp.PreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "취향 설문 API", description = "사용자 독서 취향 설문 관련 API")
public class PreferenceController {

    private final PreferenceService preferenceService;

    /**
     * 취향 설문 저장
     */
    @Operation(summary = "취향 설문 저장", description = "최초 로그인 시 사용자의 독서 취향을 저장합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "설문 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 설문 완료, 유효성 검증 실패 등)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음 (로그인 필요)")
    })
    @PostMapping
    public ResponseEntity<?> savePreference(
            @Parameter(description = "취향 설문 정보", required = true)
            @Valid @RequestBody PreferenceRequest request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("id");

        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            PreferenceResponse preferenceResponse = preferenceService.savePreference(userId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "취향 설문이 저장되었습니다");
            response.put("data", preferenceResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("취향 설문 저장 실패: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 취향 설문 수정
     */
    @Operation(summary = "취향 설문 수정", description = "기존 취향 설문 내용을 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "404", description = "취향 정보를 찾을 수 없음")
    })
    @PutMapping
    public ResponseEntity<?> updatePreference(
            @Parameter(description = "취향 설문 정보", required = true)
            @Valid @RequestBody PreferenceRequest request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("id");

        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            PreferenceResponse preferenceResponse = preferenceService.updatePreference(userId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "취향 설문이 수정되었습니다");
            response.put("data", preferenceResponse);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("취향 설문 수정 실패: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 취향 설문 조회
     */
    @Operation(summary = "취향 설문 조회", description = "현재 로그인한 사용자의 취향 설문 내용을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "404", description = "취향 정보를 찾을 수 없음")
    })
    @GetMapping
    public ResponseEntity<?> getPreference(HttpSession session) {

        Long userId = (Long) session.getAttribute("id");

        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            PreferenceResponse preferenceResponse = preferenceService.getPreference(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", preferenceResponse);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("취향 설문 조회 실패: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}