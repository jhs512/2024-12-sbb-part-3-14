package com.programmers.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record SignupDto(
        @NotBlank
        @Length(min = 6)
        String userName,

        @NotBlank
        @Pattern(regexp = "")
        String password,

        @NotBlank
        @Pattern(regexp = "")
        String passwordConfirmation,


        String email
) {
}
