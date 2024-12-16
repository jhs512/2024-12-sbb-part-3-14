package com.example.article_site.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPasswordForm {
    @NotEmpty(message = "이메일을 입력해주세요")
    private String email;

    @NotEmpty(message = "이름을 입력해주세요")
    private String username;
}
