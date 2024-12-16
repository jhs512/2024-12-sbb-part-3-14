package baekgwa.sbb.domain.question.form;

import baekgwa.sbb.global.annotation.question.QuestionContent;
import baekgwa.sbb.global.annotation.question.QuestionSubject;
import jakarta.validation.constraints.NotEmpty;
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
