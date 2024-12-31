package com.mysite.sbb.domain.question.service;


import com.mysite.sbb.domain.answer.domain.Answer;
import com.mysite.sbb.domain.answer.repository.AnswerRepository;
import com.mysite.sbb.domain.category.domain.Category;
import com.mysite.sbb.domain.category.repository.CategoryRepository;
import com.mysite.sbb.domain.comment.domain.Comment;
import com.mysite.sbb.domain.comment.service.CommentService;
import com.mysite.sbb.domain.question.domain.Question;
import com.mysite.sbb.domain.question.repository.QuestionRepository;
import com.mysite.sbb.domain.user.domain.SiteUser;
import com.mysite.sbb.domain.user.service.UserService;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.web.api.v1.question.dto.request.QuestionRequestDTO;
import com.mysite.sbb.web.api.v1.question.dto.response.QuestionDetailResponseDTO;
import com.mysite.sbb.web.api.v1.question.dto.response.QuestionListResponseDTO;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mysite.sbb.global.util.CommonUtil.validateUserPermission;

@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final AnswerRepository answerRepository;
    private final CommentService commentService;

    @Transactional(readOnly = true)
    @Override
    public Page<QuestionListResponseDTO> getList(int page, String kw) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        Specification<Question> spec = search(kw);
        return questionRepository.findAll(spec, pageable)
                .map(QuestionListResponseDTO::new);
    }

    @Transactional(readOnly = true)
    @Override
    public QuestionDetailResponseDTO getQuestionDetail(Integer id, int page, String sortKeyword) {
        Question question = findQuestionById(id);
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc(sortKeyword)));
        Page<Answer> answers = answerRepository.findByQuestion(question, pageable);

        Map<Integer, List<Comment>> commentsForAnswers = answers.stream()
                .collect(Collectors.toMap(
                        Answer::getId,
                        answer -> commentService.getCommentsForAnswer(answer.getId())
                ));

        return new QuestionDetailResponseDTO(question, answers, commentsForAnswers);
    }

    @Transactional(readOnly = true)
    @Override
    public Question getQuestion(Integer id) {
        return findQuestionById(id);
    }

    @Transactional
    @Override
    public void create(QuestionRequestDTO dto, String userName) {
        Category category = categoryRepository.findByName(dto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        SiteUser user = userService.getUser(userName);
        Question question = new Question();
        question.setSubject(dto.getSubject());
        question.setContent(dto.getContent());
        question.setCategory(category);
        question.setAuthor(user);
        questionRepository.save(question);
    }

    @Transactional
    @Override
    public void modify(Integer id, QuestionRequestDTO dto, String userName) {
        Question question = getQuestion(id);
        validateUserPermission(question.getAuthor().getUsername(), userName, "수정권한");
        Category category = categoryRepository.findByName(dto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        question.setCategory(category);
        question.setSubject(dto.getSubject());
        question.setContent(dto.getContent());
        questionRepository.save(question);
    }

    @Transactional
    @Override
    public void delete(Integer id, String userName) {
        Question question = getQuestion(id);
        validateUserPermission(question.getAuthor().getUsername(), userName, "삭제권한");
        this.questionRepository.delete(question);
    }

    @Transactional
    @Override
    public void vote(Integer id, String userName) {
        Question question = getQuestion(id);
        SiteUser siteUser = userService.getUser(userName);
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    @Override
    public Specification<Question> search(String kw) {
        return new Specification<>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Question, SiteUser> u2 = q.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"),
                        cb.like(q.get("content"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"),
                        cb.like(a.get("content"), "%" + kw + "%"),
                        cb.like(u2.get("username"), "%" + kw + "%"));
            }
        };
    }

    private Question findQuestionById(Integer id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당 질문을 찾을 수 없습니다 : " + id));
    }

    @Transactional
    public void increaseViewCount(Integer questionId) {
        Question question = findQuestionById(questionId);
        question.setViewCount(question.getViewCount() + 1);
        questionRepository.save(question); // 조회수 증가를 저장
    }
}
