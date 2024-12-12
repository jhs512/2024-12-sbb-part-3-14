package baekgwa.sbb.domain.answer.service;

import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.answer.persistence.AnswerRepository;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    @Override
    public void create(Integer id, String content, String username) {
        Question question = questionRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("question not found"));

        SiteUser siteUser = userRepository.findByUsername(username).orElseThrow(
                () -> new DataNotFoundException("site user not found"));

        Answer answer = Answer
                .builder()
                .content(content)
                .createDate(LocalDateTime.now())
                .question(question)
                .siteUser(siteUser)
                .build();

        answerRepository.save(answer);
    }

    @Override
    public Question getQuestionByIdAndAnswers(Integer id) {
        return questionRepository.findByIdWithAnswers(id).orElseThrow(
                () -> new DataNotFoundException("question not found"));
    }
}
