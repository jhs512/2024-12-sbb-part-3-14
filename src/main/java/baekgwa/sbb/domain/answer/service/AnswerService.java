package baekgwa.sbb.domain.answer.service;

import baekgwa.sbb.domain.question.dto.QuestionDto;

public interface AnswerService {
    void create(Integer id, String content, String username);

    QuestionDto.DetailInfo getQuestionByIdAndAnswers(Integer id);
}
