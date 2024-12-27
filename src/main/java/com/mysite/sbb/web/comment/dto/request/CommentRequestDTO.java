package com.mysite.sbb.web.comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CommentRequestDTO(
        @NotEmpty(message = "내용은 필수항목입니다.") String content,
        @NotNull(message = "대상 타입은 필수항목입니다.") CommentTargetType targetType,
        @NotNull(message = "대상 ID는 필수항목입니다.") Long targetId
) {
    public static CommentRequestDTO empty() {
        return new CommentRequestDTO("", null, null);
    }
}
