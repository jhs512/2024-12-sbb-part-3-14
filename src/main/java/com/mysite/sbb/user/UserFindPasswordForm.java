package com.mysite.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFindPasswordForm {
    @NotEmpty(message="ID는 필수값입니다")
    String username;

    @NotEmpty(message="이메일은 필수값입니다")
    @Email
    String email;
}