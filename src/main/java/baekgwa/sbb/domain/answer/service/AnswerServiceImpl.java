package baekgwa.sbb.domain.answer.service;

import baekgwa.sbb.domain.question.dto.QuestionDto;
import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.answer.persistence.AnswerRepository;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
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

        answerRepository.save(Answer
                .builder()
                .content(content)
                .question(question)
                .siteUser(siteUser)
                .build());
    }

    @Override
    public QuestionDto.DetailInfo getQuestionByIdAndAnswers(Integer id) {
        Question question = questionRepository.findByIdWithAnswers(id).orElseThrow(
                () -> new DataNotFoundException("question not found"));

        return QuestionDto.DetailInfo
                .builder()
                .id(question.getId())
                .subject(question.getSubject())
                .content(question.getContent())
                .answerList(question.getAnswerList())
                .createDate(question.getCreateDate())
                .author(question.getSiteUser())
                .build();
    }
}
