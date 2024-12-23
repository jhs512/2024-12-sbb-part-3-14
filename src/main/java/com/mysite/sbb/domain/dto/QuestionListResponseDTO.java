package com.mysite.sbb.domain.dto;

import com.mysite.sbb.domain.entity.Question;
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
