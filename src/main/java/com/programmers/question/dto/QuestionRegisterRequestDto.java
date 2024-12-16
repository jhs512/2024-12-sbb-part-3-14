package com.programmers.question.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionRegisterRequestDto (
        @NotBlank
        String subject,

        @NotBlank
        String content
) {
}
