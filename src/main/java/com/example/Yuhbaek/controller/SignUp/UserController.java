package com.example.Yuhbaek.controller.SignUp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import com.example.Yuhbaek.dto.SignUp.JoinRequest;
import com.example.Yuhbaek.dto.SignUp.LoginRequest;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import com.example.Yuhbaek.service.SignUp.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "인증 API", description = "회원가입, 로그인, 로그아웃 등 인증 관련 API")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (중복된 아이디/닉네임/이메일, 비밀번호 불일치 등)")
    })
    @PostMapping("/join")
    public ResponseEntity<?> join(
            @Parameter(description = "회원가입 정보", required = true)
            @Valid @RequestBody JoinRequest request) {
        try {
            UserEntity user = userService.join(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다");
            response.put("id", user.getId());
            response.put("userId", user.getUserId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("회원가입 실패: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 아이디/비밀번호)")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "로그인 정보", required = true)
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        try {
            UserEntity user = userService.login(request);

            // 세션에 사용자 정보 저장
            session.setAttribute("id", user.getId());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", user.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("id", user.getId());
            response.put("userId", user.getUserId());
            response.put("name", user.getName());
            response.put("nickname", user.getNickname());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("surveyCompleted", user.isSurveyCompleted());  // ⭐ 추가

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("로그인 실패: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃", description = "현재 세션을 종료합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "로그아웃 되었습니다");

        return ResponseEntity.ok(response);
    }

    /**
     * 아이디 중복 체크
     */
    @Operation(summary = "아이디 중복 체크", description = "아이디가 사용 가능한지 확인합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "확인 완료")
    })
    @GetMapping("/check/userId")
    public ResponseEntity<?> checkUserId(
            @Parameter(description = "확인할 아이디", required = true, example = "user123")
            @RequestParam String userId) {
        boolean available = userService.isUserIdAvailable(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("message", available ? "사용 가능한 아이디입니다" : "이미 사용 중인 아이디입니다");

        return ResponseEntity.ok(response);
    }

    /**
     * 닉네임 중복 체크
     */
    @Operation(summary = "닉네임 중복 체크", description = "닉네임이 사용 가능한지 확인합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "확인 완료")
    })
    @GetMapping("/check/nickname")
    public ResponseEntity<?> checkNickname(
            @Parameter(description = "확인할 닉네임", required = true, example = "길동이")
            @RequestParam String nickname) {
        boolean available = userService.isNicknameAvailable(nickname);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("message", available ? "사용 가능한 닉네임입니다" : "이미 사용 중인 닉네임입니다");

        return ResponseEntity.ok(response);
    }

    /**
     * 이메일 중복 체크
     */
    @Operation(summary = "이메일 중복 체크", description = "이메일이 사용 가능한지 확인합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "확인 완료")
    })
    @GetMapping("/check/email")
    public ResponseEntity<?> checkEmail(
            @Parameter(description = "확인할 이메일", required = true, example = "user@example.com")
            @RequestParam String email) {
        boolean available = userService.isEmailAvailable(email);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("message", available ? "사용 가능한 이메일입니다" : "이미 사용 중인 이메일입니다");

        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @Operation(summary = "현재 사용자 정보", description = "현재 로그인한 사용자의 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음 (로그인 필요)")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long id = (Long) session.getAttribute("id");

        if (id == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            UserEntity user = userService.findByUserId((String) session.getAttribute("userId"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", user.getId());
            response.put("userId", user.getUserId());
            response.put("name", user.getName());
            response.put("nickname", user.getNickname());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}