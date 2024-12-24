package com.mysite.sbb.comment.entity;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;

    private Integer questionId;
    private Integer parentId;
    private String parentContent;
}
