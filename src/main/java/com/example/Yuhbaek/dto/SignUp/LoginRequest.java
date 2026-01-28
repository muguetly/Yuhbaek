package com.example.Yuhbaek.dto.SignUp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그인 요청")
public class LoginRequest {

    @Schema(description = "아이디", example = "user123")
    @NotBlank(message = "아이디는 필수입니다")
    private String userId;

    @Schema(description = "비밀번호", example = "Test1234!")
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    @Schema(description = "자동 로그인", example = "false")
    private boolean rememberMe = false;
}