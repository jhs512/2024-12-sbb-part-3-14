package com.programmers.question.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record QuestionModifyRequestDto (
        @NotBlank
        String subject,

        @NotBlank
        String content
){
}
