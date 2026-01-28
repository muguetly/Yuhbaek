package com.example.Yuhbaek.dto.SignUp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 요청")
public class JoinRequest {

    @Schema(description = "아이디", example = "user123")
    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 2, max = 8, message = "아이디는 2~8자 사이여야 합니다")
    private String userId;

    @Schema(description = "비밀번호 (대소문자, 숫자, 특수문자 포함 8자 이상)", example = "Test1234!")
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다")
    private String password;

    @Schema(description = "비밀번호 확인", example = "Test1234!")
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String passwordConfirm;

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 20, message = "이름은 20자 이하여야 합니다")
    private String name;

    @Schema(description = "닉네임", example = "길동이")
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다")
    private String nickname;

    @Schema(description = "이메일", example = "user@example.com")
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
}