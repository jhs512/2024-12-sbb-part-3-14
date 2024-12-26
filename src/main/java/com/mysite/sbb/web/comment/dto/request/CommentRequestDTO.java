package com.mysite.sbb.web.comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CommentRequestDTO {
    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;
    private String targetType; // "question" or "answer"
    private Integer targetId;
    private Integer parentId;

}
