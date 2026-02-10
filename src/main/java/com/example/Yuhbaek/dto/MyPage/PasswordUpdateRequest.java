package com.example.Yuhbaek.dto.MyPage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 변경 요청")
public class PasswordUpdateRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다")
    @Schema(description = "현재 비밀번호", example = "OldPassword123!")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다")
    @Schema(description = "새 비밀번호", example = "NewPassword123!")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수입니다")
    @Schema(description = "새 비밀번호 확인", example = "NewPassword123!")
    private String newPasswordConfirm;
}