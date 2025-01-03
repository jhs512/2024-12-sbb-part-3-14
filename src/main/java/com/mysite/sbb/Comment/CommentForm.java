package com.mysite.sbb.Comment;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

    @NotEmpty(message = "댓글 내용을 입력해야 합니다.")
    private String content;
}