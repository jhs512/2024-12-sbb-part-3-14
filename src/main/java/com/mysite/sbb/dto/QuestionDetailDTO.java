package com.mysite.sbb.dto;

import com.mysite.sbb.domain.Answer;
import com.mysite.sbb.domain.Question;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class QuestionDetailDTO extends BaseQuestionDTO {
    private int voterCount;              // 추천 수
    private List<AnswerDTO> answers;        // 답변 리스트

    public QuestionDetailDTO(Question question, List<Answer> answers) {
        super(question);
        this.voterCount = question.getVoter().size();   // 추천 개수
        this.answers = answers.stream()
                .map(AnswerDTO::new)
                .collect(Collectors.toList());
    }
}
