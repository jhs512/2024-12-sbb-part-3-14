package baekgwa.sbb.domain.question;

import baekgwa.sbb.model.question.Question;
import java.util.List;

public interface QuestionService {
    List<QuestionDto.MainInfo> getList();

    QuestionDto.DetailInfo getQuestion(Integer id);

    void create(String subject, String content);
}
