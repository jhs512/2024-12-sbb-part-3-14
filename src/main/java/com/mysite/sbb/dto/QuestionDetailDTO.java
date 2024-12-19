package com.mysite.sbb.dto;

import com.mysite.sbb.domain.Answer;
import com.mysite.sbb.domain.Question;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class QuestionDetailDTO extends BaseQuestionDTO {
    private int voterCount;              // 추천 수
    private Page<AnswerDTO> answers;        // 답변 리스트

    public QuestionDetailDTO(Question question, Page<Answer> answers) {
        super(question);
        this.voterCount = question.getVoter().size();   // 추천 개수
        this.answers = answers.map(AnswerDTO::new);
    }
}
