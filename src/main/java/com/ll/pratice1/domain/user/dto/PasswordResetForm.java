package com.ll.pratice1.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetForm {
    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password_reset;

    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String password_reset_check;
}