package baekgwa.sbb.domain.question.service;

import baekgwa.sbb.domain.question.dto.QuestionDto;
import baekgwa.sbb.domain.question.form.QuestionForm;
import java.util.List;
import org.springframework.data.domain.Page;

public interface QuestionService {

    QuestionDto.DetailInfo getQuestion(Integer id, String loginUsername);

    void create(String subject, String content, String username);

    Page<QuestionDto.MainInfo> getList(int page, int size);

    void modifyQuestion(Integer questionId, String loginUsername, QuestionForm questionForm);

    void deleteQuestion(Integer questionId, String loginUsername);

    void vote(Integer questionId, String loginUsername);

    void voteCancel(Integer questionId, String loginUsername);
}
