package baekgwa.sbb.domain.question.service;

import baekgwa.sbb.domain.answer.dto.AnswerDto;
import baekgwa.sbb.domain.question.dto.QuestionDto;
import baekgwa.sbb.domain.question.form.QuestionForm;
import baekgwa.sbb.global.exception.DataNotFoundException;
import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.answer.persistence.AnswerRepository;
import baekgwa.sbb.model.category.entity.Category;
import baekgwa.sbb.model.category.entity.CategoryType;
import baekgwa.sbb.model.category.persistence.CategoryRepository;
import baekgwa.sbb.model.comment.entity.Comment;
import baekgwa.sbb.model.comment.persistence.CommentRepository;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.question.persistence.QuestionRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public QuestionDto.DetailInfo getQuestion(Integer id, String loginUsername, Integer page,
            Integer size) {
        Question question = getQuestionById(id);
        List<Comment> questionCommentList = commentRepository.findByQuestion(question);
        Page<Answer> answerPage = getAnswersForQuestion(id, page, size);

        incrementViewCount(question.getId());

        return buildQuestionDetailDto(question, loginUsername, answerPage, questionCommentList);
    }


    @Transactional(readOnly = true)
    @Override
    public QuestionDto.ModifyInfo getQuestion(Integer id) {
        Question question = questionRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("site user not found"));

        return QuestionDto.ModifyInfo
                .builder()
                .subject(question.getSubject())
                .content(question.getContent())
                .build();
    }

    @Transactional
    @Override
    public void create(String subject, String content, String username) {
        SiteUser siteUser = userRepository.findByUsername(username).orElseThrow(
                () -> new DataNotFoundException("site user not found"));

        Category category = categoryRepository.findByCategoryType(CategoryType.QUESTION)
                .orElseThrow(
                        () -> new DataNotFoundException("Question Category not found"));

        questionRepository.save(
                Question.builder()
                        .subject(subject)
                        .content(content)
                        .siteUser(siteUser)
                        .category(category)
                        .build());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<QuestionDto.MainInfo> getList(int page, int size, String keyword,
            String categoryType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createDate")));
        Specification<Question> spec = QuestionSpecificationBuilder.INSTANCE.searchByKeywordAndCategoryType(
                keyword, categoryType);

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

        if(!checkPermission(findData.getSiteUser().getUsername(), loginUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        findData.modifyQuestion(questionForm.getSubject(), questionForm.getContent());
    }

    @Transactional
    @Override
    public void deleteQuestion(Integer questionId, String loginUsername) {
        Question findData = questionRepository.findByIdWithAnswersAndSiteUserAndVoter(questionId)
                .orElseThrow(
                        () -> new DataNotFoundException("question not found"));

        if(!checkPermission(findData.getSiteUser().getUsername(), loginUsername)) {
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

    @Transactional(readOnly = true)
    @Override
    public List<QuestionDto.CategoryInfo> getCategory() {
        List<Category> findList = categoryRepository.findAll();

        return findList.stream().map(
                c -> QuestionDto.CategoryInfo
                        .builder()
                        .categoryType(c.getCategoryType().name())
                        .build()
        ).toList();
    }

    private Question getQuestionById(Integer id) {
        return questionRepository.findByIdWithSiteUserAndVoter(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
    }

    private Page<Answer> getAnswersForQuestion(Integer id, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createDate")));
        return answerRepository.findByQuestionIdOrderByVoterCountDesc(id, pageable);
    }

    private QuestionDto.DetailInfo buildQuestionDetailDto(
            Question question,
            String loginUsername,
            Page<Answer> answerPage,
            List<Comment> questionCommentList) {

        return QuestionDto.DetailInfo.builder()
                .id(question.getId())
                .subject(question.getSubject())
                .content(question.getContent())
                .createDate(question.getCreateDate())
                .modifyDate(question.getModifyDate())
                .author(question.getSiteUser().getUsername())
                .voterCount(getVoterCount(question))
                .userVote(isUserVoted(question, loginUsername))
                .answerList(getAnswerDtos(answerPage, loginUsername))
                .questionCommentList(getCommentDtos(questionCommentList))
                .viewCount(question.getViewCount())
                .build();
    }

    private long getVoterCount(Question question) {
        return question.getVoter().stream().count();
    }

    private boolean isUserVoted(Question question, String loginUsername) {
        return question.getVoter().stream()
                .anyMatch(voter -> voter.getUsername().equals(loginUsername));
    }

    private Page<AnswerDto.AnswerDetailInfo> getAnswerDtos(Page<Answer> answerPage,
            String loginUsername) {
        List<AnswerDto.AnswerDetailInfo> answerDetailInfoList = answerPage.stream()
                .map(answer -> buildAnswerDto(answer, loginUsername))
                .collect(Collectors.toList());

        return new PageImpl<>(answerDetailInfoList, answerPage.getPageable(),
                answerPage.getTotalElements());
    }


    private AnswerDto.AnswerDetailInfo buildAnswerDto(Answer answer, String loginUsername) {
        return AnswerDto.AnswerDetailInfo.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .modifyDate(answer.getModifyDate())
                .createDate(answer.getCreateDate())
                .author(answer.getSiteUser().getUsername())
                .voteCount(getVoterCount(answer))
                .userVote(isUserVoted(answer, loginUsername))
                .build();
    }

    private long getVoterCount(Answer answer) {
        return answer.getVoter().stream().count();
    }

    private boolean isUserVoted(Answer answer, String loginUsername) {
        return answer.getVoter().stream()
                .anyMatch(voter -> voter.getUsername().equals(loginUsername));
    }

    private List<QuestionDto.QuestionCommentInfo> getCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(comment -> QuestionDto.QuestionCommentInfo.builder()
                        .id(comment.getId())
                        .author(comment.getSiteUser().getUsername())
                        .content(comment.getContent())
                        .createDate(comment.getCreateDate())
                        .build())
                .collect(Collectors.toList());
    }

    private void incrementViewCount(Integer questionId) {
        //todo : 동시성 문제 test 코드 작성
        //todo : 새로고침 시, 무한정 늘어나도록 설정되어있는 것, 처리 필요.
        //todo : 유튜브 처럼, 수십만의 조회수가 몰리면, 이렇게 처리 보다는, 벌크성 쿼리 혹은 batch 작업 필요.
        questionRepository.incrementViewCount(questionId);
    }

    /**
     *
     * @param authorName
     * @param loginName
     * @return booelean
     */
    private boolean checkPermission(String authorName, String loginName) {
        return authorName.equals(loginName);
    }
}
