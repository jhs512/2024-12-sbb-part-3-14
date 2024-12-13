package baekgwa.sbb.domain.question.service;

import baekgwa.sbb.domain.question.dto.QuestionDto;
import baekgwa.sbb.domain.question.form.QuestionForm;
import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
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

    @Deprecated
    @Transactional(readOnly = true)
    @Override
    public List<QuestionDto.MainInfo> getList() {
        return questionRepository.findAll()
                .stream().map(
                        data -> QuestionDto.MainInfo
                                .builder()
                                .id(data.getId())
                                .subject(data.getSubject())
                                .createDate(data.getCreateDate())
                                .build()
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public QuestionDto.DetailInfo getQuestion(Integer id) {
        Question question = questionRepository.findByIdWithAnswers(id).orElseThrow(
                () -> new DataNotFoundException("question not found"));

        return QuestionDto.DetailInfo
                .builder()
                .id(question.getId())
                .subject(question.getSubject())
                .answerList(question.getAnswerList())
                .content(question.getContent())
                .createDate(question.getCreateDate())
                .author(question.getSiteUser())
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
                                .answerList(question.getAnswerList())
                                .author(question.getSiteUser())
                                .build()
                );
    }

    @Transactional
    @Override
    public void modifyQuestion(Integer questionId, String loginUsername,
            QuestionForm questionForm) {
        Question findData = questionRepository.findByIdWithAnswers(questionId).orElseThrow(
                () -> new DataNotFoundException("question not found"));

        questionRepository.save(
                Question.modifyQuestion(
                        findData, questionForm.getSubject(), questionForm.getContent()
                ));
    }

    @Transactional
    @Override
    public void deleteQuestion(Integer questionId, String loginUsername) {
        Question findData = questionRepository.findByIdWithAnswers(questionId).orElseThrow(
                () -> new DataNotFoundException("question not found"));

        if (!findData.getSiteUser().getUsername().equals(loginUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        questionRepository.delete(findData);
    }
}
