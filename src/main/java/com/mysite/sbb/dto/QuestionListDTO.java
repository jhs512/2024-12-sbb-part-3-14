package com.mysite.sbb.dto;

import com.mysite.sbb.domain.Question;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionListDTO extends BaseQuestionDTO{
    public QuestionListDTO(Question question) {
        super(question);
    }
}
