package com.programmers.answer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AnswerRegisterRequestDto(
        @Min(1)
        long questionId,

        @NotBlank
        String content
) {
}
