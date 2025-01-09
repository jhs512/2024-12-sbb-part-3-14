package com.programmers.question.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record QuestionRegisterRequestDto (
        @NotBlank(message = "제목은 공백일 수 없습니다.")
        String subject,

        @NotBlank(message = "내용은 공백일 수 없습니다.")
        String content
) {
}
