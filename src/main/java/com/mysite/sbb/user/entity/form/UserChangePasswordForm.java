package com.mysite.sbb.user.entity.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangePasswordForm {

    @NotEmpty(message = "현재 비밀번호는 필수항목입니다.")
    private String password1;

    @NotEmpty(message = "변경 비밀번호는 필수항목입니다.")
    private String password2;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password3;

    private String username;
}
