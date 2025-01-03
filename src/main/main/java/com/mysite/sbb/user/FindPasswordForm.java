package com.mysite.sbb.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPasswordForm {

    @NotEmpty(message = "이메일은 필수 항목입니다.")
    private String email;
}
