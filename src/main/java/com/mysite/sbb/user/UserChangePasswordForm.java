package com.mysite.sbb.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangePasswordForm {
    @NotEmpty(message = "기존 비밀번호는 필수값입니다.")
    private String oldPassword;

    @NotEmpty(message = "새 비밀번호는 필수값입니다.")
    private String newPassword1;

    @NotEmpty(message = "비밀번호 확인은 필수값입니다.")
    private String newPassword2;
}
