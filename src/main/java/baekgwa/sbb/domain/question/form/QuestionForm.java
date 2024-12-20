package baekgwa.sbb.domain.question.form;

import baekgwa.sbb.global.annotation.validation.question.QuestionContent;
import baekgwa.sbb.global.annotation.validation.question.QuestionSubject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {

    @QuestionSubject
    private String subject;

    @QuestionContent
    private String content;
}
