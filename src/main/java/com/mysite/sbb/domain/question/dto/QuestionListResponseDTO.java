package com.mysite.sbb.domain.question.dto;

import com.mysite.sbb.domain.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionListResponseDTO extends QuestionBaseDTO {
    public QuestionListResponseDTO(Question question) {
        super(question);
    }
}
