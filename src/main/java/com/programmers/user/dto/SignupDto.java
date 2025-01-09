package com.programmers.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record SignupDto(
        @NotBlank
        @Length(min = 6)
        String userName,

        @NotBlank
        String password,

        @NotBlank
        String passwordConfirmation,

        @NotBlank
        String email
) {
}
