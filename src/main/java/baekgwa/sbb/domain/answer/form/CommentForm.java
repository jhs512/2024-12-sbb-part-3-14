package baekgwa.sbb.domain.answer.form;

import baekgwa.sbb.global.annotation.validation.comment.CommentContent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

    @CommentContent
    private String content;
}
