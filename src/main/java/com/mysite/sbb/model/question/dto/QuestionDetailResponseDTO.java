package com.mysite.sbb.model.question.dto;

import com.mysite.sbb.model.answer.entity.Answer;
import com.mysite.sbb.model.answer.dto.AnswerResponseDTO;
import com.mysite.sbb.model.question.entity.Question;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class QuestionDetailResponseDTO extends QuestionBaseDTO {
    private int voterCount;              // 추천 수
    private Page<AnswerResponseDTO> answers;        // 답변 리스트

    public QuestionDetailResponseDTO(Question question, Page<Answer> answers) {
        super(question);
        this.voterCount = question.getVoter().size();   // 추천 개수
        this.answers = answers.map(AnswerResponseDTO::new);
    }
}
