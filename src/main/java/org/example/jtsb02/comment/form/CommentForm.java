package org.example.jtsb02.comment.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

    @NotEmpty(message = "내용은 필수항목입니다.")
    @Size(max = 2000)
    private String content;
}
