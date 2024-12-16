package baekgwa.sbb.domain.answer.service;

import baekgwa.sbb.domain.answer.dto.AnswerDto;
import baekgwa.sbb.domain.question.dto.QuestionDto;

public interface AnswerService {
    void create(Integer id, String content, String username);

    QuestionDto.DetailInfo getQuestionByIdAndAnswers(Integer id, String loginUsername);

    AnswerDto.AnswerInfo getAnswer(Integer answerId);

    Integer modifyAnswer(Integer answerId, String loginUsername, String newContent);

    Integer deleteAnswer(Integer answerId, String loginUsername);

    Integer vote(Integer answerId, String loginUsername);

    Integer voteCancel(Integer answerId, String loginUsername);
}
