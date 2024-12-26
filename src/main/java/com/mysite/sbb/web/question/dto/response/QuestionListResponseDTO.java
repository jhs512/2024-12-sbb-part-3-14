package com.mysite.sbb.web.question.dto.response;

import com.mysite.sbb.web.common.dto.QuestionBaseDTO;
import com.mysite.sbb.domain.question.Question;
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
