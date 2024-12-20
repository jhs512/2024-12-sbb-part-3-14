package com.mysite.sbb.user.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
    @Size(min = 3, max = 25)
    @NotEmpty(message = "ID는 필수 항목")
    private String username;

    @NotEmpty(message = "PW는 필수 항목 입니다.")
    private String password;

    @NotEmpty(message = "PW 확인은 필수 항목 입니다.")
    private String confirmPassword;

    @NotEmpty(message = "email은 필수 항목 입니다.")
    @Email
    private String email;
}
