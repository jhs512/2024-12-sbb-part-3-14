package com.mysite.sbb.user.entity;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordForm {

    @NotEmpty(message = "현재 비밀번호는 필수항목입니다.")
    private String currentPassword;

    @NotEmpty(message = "새 비밀번호는 필수항목입니다.")
    private String newPassword;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String confirmPassword;
}
