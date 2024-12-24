package com.mysite.sbb.comment.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    @NotEmpty(message = "댓글을 입력해 주세요. ")
    private String content;
}
