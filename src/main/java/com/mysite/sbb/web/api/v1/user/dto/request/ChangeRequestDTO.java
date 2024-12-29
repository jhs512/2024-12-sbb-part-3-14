package com.mysite.sbb.web.api.common.v1.user.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record ChangeRequestDTO(
        @NotEmpty(message = "비밀번호는 필수항목입니다.")
        String currentPassword,

        @NotEmpty(message = "비밀번호는 필수항목입니다.")
        String newPassword,

        @NotEmpty(message = "비밀번호는 필수항목입니다.")
        String confirmNewPassword
) {
}


