package baekgwa.sbb.domain.answer.service;

import baekgwa.sbb.domain.answer.dto.AnswerDto;
import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.answer.persistence.AnswerRepository;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    @Override
    public Integer create(Integer id, String content, String username) {
        Question question = questionRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("question not found"));

        SiteUser siteUser = userRepository.findByUsername(username).orElseThrow(
                () -> new DataNotFoundException("site user not found"));

        Answer saveAnswer = answerRepository.save(Answer
                .builder()
                .content(content)
                .question(question)
                .siteUser(siteUser)
                .build());

        return saveAnswer.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public AnswerDto.AnswerInfo getAnswer(Integer answerId) {
        Answer findData = answerRepository.findByIdWithSiteUser(answerId).orElseThrow(
                () -> new DataNotFoundException("Answer not found"));

        return AnswerDto.AnswerInfo
                .builder()
                .content(findData.getContent())
                .username(findData.getSiteUser().getUsername())
                .build();
    }

    /**
     *
     * @param answerId
     * @param loginUsername
     * @param newContent
     * @return 수정한 QuestionID 반환
     */
    @Transactional
    @Override
    public Integer modifyAnswer(Integer answerId, String loginUsername, String newContent) {
        Answer findData = answerRepository.findByIdWithQuestion(answerId).orElseThrow(
                () -> new DataNotFoundException("Answer not found"));

        if (!checkPermission(findData.getSiteUser().getUsername(), loginUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        answerRepository.save(Answer.modifyAnswer(findData, newContent));
        return findData.getQuestion().getId();
    }

    /**
     *
     * @param answerId
     * @param loginUsername
     * @return 연관된 Question ID 반환
     */
    @Transactional
    @Override
    public Integer deleteAnswer(Integer answerId, String loginUsername) {
        Answer findData = answerRepository.findByIdWithQuestion(answerId).orElseThrow(
                () -> new DataNotFoundException("answer not found"));

        if (!checkPermission(findData.getSiteUser().getUsername(), loginUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        Integer questionId = findData.getQuestion().getId();
        answerRepository.deleteById(answerId);

        return questionId;
    }

    @Transactional
    @Override
    public Integer vote(Integer answerId, String loginUsername) {
        Answer answer = answerRepository.findByIdWithQuestionAndSiteUser(answerId).orElseThrow(
                () -> new DataNotFoundException("answer not found"));
        SiteUser siteUser = userRepository.findByUsername(loginUsername).orElseThrow(
                () -> new DataNotFoundException("answer not found"));

        answer.getVoter().add(siteUser);
        answerRepository.save(answer);

        return answer.getQuestion().getId();
    }

    @Transactional
    @Override
    public Integer voteCancel(Integer answerId, String loginUsername) {
        Answer answer = answerRepository.findByIdWithVoterAndQuestion(answerId).orElseThrow(
                () -> new DataNotFoundException("Answer not found"));

        SiteUser siteUser = userRepository.findByUsername(loginUsername).orElseThrow(
                () -> new DataNotFoundException("user not found"));

        answer.getVoter().remove(siteUser);
        return answer.getQuestion().getId();
    }

    /**
     * 로그인 이름과, 답변의 주인의 매칭여부로, 권한 여부를 판단합니다.
     * @param authorName
     * @param loginName
     * @return boolean
     */
    private boolean checkPermission(String authorName, String loginName) {
        return authorName.equals(loginName);
    }
}
