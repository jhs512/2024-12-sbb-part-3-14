package com.example.article_site.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyPasswordForm {

    @NotEmpty(message = "기존 비밀번호를 입력하세요")
    private String oldPassword;

    @NotEmpty(message = "새로운 비밀번호를 입력하세요")
    private String newPassword;

    @NotEmpty(message = "확인을 위해 비밀번호를 한번 더 입력해야합니다.")
    private String checkPassword;
}
