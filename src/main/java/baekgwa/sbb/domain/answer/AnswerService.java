package baekgwa.sbb.domain.answer;

import baekgwa.sbb.model.question.Question;

public interface AnswerService {
    void create(Integer id, String content);

    Question getQuestionByIdAndAnswers(Integer id);
}
