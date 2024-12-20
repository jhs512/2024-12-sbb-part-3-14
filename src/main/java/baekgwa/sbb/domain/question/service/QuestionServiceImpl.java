package baekgwa.sbb.domain.question.service;

import baekgwa.sbb.domain.answer.dto.AnswerDto;
import baekgwa.sbb.domain.question.dto.QuestionDto;
import baekgwa.sbb.domain.question.form.QuestionForm;
import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.answer.persistence.AnswerRepository;
import baekgwa.sbb.model.comment.entity.Comment;
import baekgwa.sbb.model.comment.persistence.CommentRepository;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public QuestionDto.DetailInfo getQuestion(Integer id, String loginUsername, Integer page,
            Integer size) {
        Question question = questionRepository.findByIdWithSiteUserAndVoter(id)
                .orElseThrow(
                        () -> new DataNotFoundException("question not found"));

        List<Comment> questionCommentList = commentRepository.findByQuestion(question);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createDate")));
        Page<Answer> answer = answerRepository.findByQuestionIdOrderByVoterCountDesc(id, pageable);

        return QuestionDto.DetailInfo
                .builder()
                .id(question.getId())
                .subject(question.getSubject())
                .content(question.getContent())
                .createDate(question.getCreateDate())
                .modifyDate(question.getModifyDate())
                .author(question.getSiteUser().getUsername())
                .voterCount(question.getVoter().stream().count())
                .userVote(question.getVoter().stream()
                        .anyMatch(voter -> voter.getUsername().equals(loginUsername)))
                .answerList(
                        answer.map(data -> AnswerDto.AnswerDetailInfo
                                        .builder()
                                        .id(data.getId())
                                        .content(data.getContent())
                                        .modifyDate(data.getModifyDate())
                                        .createDate(data.getCreateDate())
                                        .author(data.getSiteUser().getUsername())
                                        .voteCount(data.getVoter().stream().count())
                                        .userVote(data.getVoter().stream().anyMatch(
                                                voter -> voter.getUsername().equals(loginUsername)))
                                        .build()))
                .questionCommentList(
                        questionCommentList.stream().map(
                                comment -> QuestionDto.QuestionCommentInfo
                                        .builder()
                                        .id(comment.getId())
                                        .author(comment.getSiteUser().getUsername())
                                        .content(comment.getContent())
                                        .createDate(comment.getCreateDate())
                                        .build()).toList())
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
    public Page<QuestionDto.MainInfo> getList(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createDate")));
        Specification<Question> spec = QuestionSpecificationBuilder.INSTANCE.searchByKeyword(keyword);

        return questionRepository.findAll(spec, pageable)
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

    @Transactional
    @Override
    public void createComment(String content, String loginUsername, Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new DataNotFoundException("question not found"));
        SiteUser author = userRepository.findByUsername(loginUsername).orElseThrow(
                () -> new DataNotFoundException("user not found"));

        Comment newComment = Comment
                .builder()
                .question(question)
                .content(content)
                .siteUser(author)
                .build();

        commentRepository.save(newComment);
    }
}
