package com.mysite.sbb.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordForm {


    @NotEmpty(message="비밀번호를 입력해주세요")
    @Size(min = 8, max = 30)
    private String password;

    @NotEmpty(message="새비밀번호를 입력해주세요")
    @Size(min = 8, max = 30)
    private String newpassword;

    @NotEmpty(message="비밀번호확인을 입력해주세요")
    @Size(min = 8, max = 30)
    private String newpasswordcheck;
}
