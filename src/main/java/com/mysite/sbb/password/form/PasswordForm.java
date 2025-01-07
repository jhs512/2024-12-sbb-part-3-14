package com.mysite.sbb.password.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordForm {
    @NotEmpty(message = "임시 비밀번호를 입력받을 이메일을 입력하세요. ")
    @Email(message = "email 형식으로 입력 해 주세요. ")
    private String email;
}
