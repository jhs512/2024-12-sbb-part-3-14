package com.programmers.answer.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerRegisterRequestDto(
        @NotBlank
        String content
) {
}
