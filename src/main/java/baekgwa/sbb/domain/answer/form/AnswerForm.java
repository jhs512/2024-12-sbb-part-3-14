package baekgwa.sbb.domain.answer.form;

import baekgwa.sbb.global.annotation.answer.AnswerContent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerForm {

    @AnswerContent
    private String content;
}
