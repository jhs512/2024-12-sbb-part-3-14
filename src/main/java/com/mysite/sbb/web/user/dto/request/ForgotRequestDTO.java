package com.mysite.sbb.web.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ForgotRequestDTO(
        @Size(min = 3, max = 25)
        @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
        String username,

        @NotEmpty(message = "이메일은 필수항목입니다.")
        @Email
        String email
) {
}
