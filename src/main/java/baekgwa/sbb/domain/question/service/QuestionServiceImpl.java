package baekgwa.sbb.domain.question.service;

import baekgwa.sbb.domain.answer.dto.AnswerDto;
import baekgwa.sbb.domain.question.dto.QuestionDto;
import baekgwa.sbb.domain.question.form.QuestionForm;
import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public QuestionDto.DetailInfo getQuestion(Integer id, String loginUsername) {
        Question question = questionRepository.findByIdWithAnswersAndSiteUserAndVoter(id)
                .orElseThrow(
                        () -> new DataNotFoundException("question not found"));

        return QuestionDto.DetailInfo
                .builder()
                .id(question.getId())
                .subject(question.getSubject())
                .content(question.getSubject())
                .createDate(question.getCreateDate())
                .modifyDate(question.getModifyDate())
                .author(question.getSiteUser().getUsername())
                .voterCount(question.getVoter().stream().count())
                .userVote(question.getVoter().stream()
                        .anyMatch(voter -> voter.getUsername().equals(loginUsername)))
                .answerList(
                        question.getAnswerList().stream().map(
                                answer -> AnswerDto.AnswerDetailInfo
                                        .builder()
                                        .id(answer.getId())
                                        .content(answer.getContent())
                                        .modifyDate(answer.getModifyDate())
                                        .createDate(answer.getCreateDate())
                                        .author(answer.getSiteUser().getUsername())
                                        .voteCount(answer.getVoter().stream().count())
                                        .userVote(answer.getVoter().stream()
                                                .anyMatch(voter -> voter.getUsername()
                                                        .equals(loginUsername)))
                                        .build()
                        ).toList()
                )
                .build();
    }

    @Transactional
    @Override
    public void create(String subject, String content, String username) {
        SiteUser siteUser = userRepository.findByUsername(username).orElseThrow(
                () -> new DataNotFoundException("site user not found"));

        questionRepository.save(
                Question.builder()
                        .subject(subject)
                        .content(content)
                        .siteUser(siteUser)
                        .build());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<QuestionDto.MainInfo> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createDate")));
        return questionRepository.findAll(pageable)
                .map(
                        question -> QuestionDto.MainInfo
                                .builder()
                                .id(question.getId())
                                .subject(question.getSubject())
                                .createDate(question.getCreateDate())
                                .answerCount(question.getAnswerList().stream().count())
                                .author(question.getSiteUser().getUsername())
                                .build()
                );
    }

    @Transactional
    @Override
    public void modifyQuestion(Integer questionId, String loginUsername,
            QuestionForm questionForm) {
        Question findData = questionRepository.findByIdWithAnswersAndSiteUserAndVoter(questionId)
                .orElseThrow(
                        () -> new DataNotFoundException("question not found"));

        questionRepository.save(
                Question.modifyQuestion(
                        findData, questionForm.getSubject(), questionForm.getContent()
                ));
    }

    @Transactional
    @Override
    public void deleteQuestion(Integer questionId, String loginUsername) {
        Question findData = questionRepository.findByIdWithAnswersAndSiteUserAndVoter(questionId)
                .orElseThrow(
                        () -> new DataNotFoundException("question not found"));

        if (!findData.getSiteUser().getUsername().equals(loginUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        questionRepository.deleteById(questionId);
    }

    @Transactional
    @Override
    public void vote(Integer questionId, String loginUsername) {
        Question question = questionRepository.findByIdWithSiteUser(questionId).orElseThrow(
                () -> new DataNotFoundException("question not found"));

        SiteUser siteUser = userRepository.findByUsername(loginUsername).orElseThrow(
                () -> new DataNotFoundException("user not found"));

        question.getVoter().add(siteUser);
        questionRepository.save(question);
    }

    @Transactional
    @Override
    public void voteCancel(Integer questionId, String loginUsername) {
        Question question = questionRepository.findByIdWithVoter(questionId).orElseThrow(
                () -> new DataNotFoundException("question not found"));

        SiteUser siteUser = userRepository.findByUsername(loginUsername).orElseThrow(
                () -> new DataNotFoundException("user not found"));

        question.getVoter().remove(siteUser);
    }
}
