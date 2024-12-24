package com.programmers.answer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AnswerModifyRequestDto (
        @NotBlank
        String content
){
}
