package com.programmers.question.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionModifyRequestDto (
        @NotBlank
        String newSubject,

        @NotBlank
        String newContent
){
}
