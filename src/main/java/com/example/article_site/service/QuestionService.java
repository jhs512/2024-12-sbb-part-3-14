package com.example.article_site.service;

import com.example.article_site.domain.Answer;
import com.example.article_site.domain.Author;
import com.example.article_site.domain.Category;
import com.example.article_site.domain.Question;
import com.example.article_site.dto.QuestionDetailDto;
import com.example.article_site.dto.QuestionListDto;
import com.example.article_site.dto.profile.QuestionProfileDto;
import com.example.article_site.exception.DataNotFoundException;
import com.example.article_site.repository.QuestionRepository;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.example.article_site.controller.SortPreference.SORT_LATEST;
import static com.example.article_site.domain.Question.modifyQuestion;
import static com.example.article_site.dto.QuestionDetailDto.createQuestionDetailDto;
import static com.example.article_site.exception.Message.QUESTION_NOT_FOUND;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    private final static int QUESTION_PAGE_SIZE = 10;
    private final static int ANSWER_PAGE_SIZE = 5;

    /**
     * question 페이지를 QuestionListDto 정보로 변환하여 넘겨준다.
     */
    public Page<QuestionListDto> getQuestionDtoPage(int page, String kw, String categoryName) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC, "createDate"));
        Pageable pageRequest = PageRequest.of(page, QUESTION_PAGE_SIZE, Sort.by(sorts));

        if(categoryName.equals("전체")){
            return questionRepository.findAll(search(kw), pageRequest).map(QuestionListDto::createQuestionListDto);
        }else{
            Specification<Question> spec = (root, query, cb) -> {
                Category category = categoryService.findByName(categoryName);
                return cb.and(
                        search(kw).toPredicate(root, query, cb),
                        cb.equal(root.get("category"), category)
                );
            };
            return questionRepository.findAll(spec, pageRequest)
                    .map(QuestionListDto::createQuestionListDto);
        }
    }

    /**
     * 상세 페이지로 보낼 id에 맞는 질문 정보를 취합하여 반환
     * 조회되지 않는 질문의 경우 DataNotFoundException 을 던진다.
     */
    public QuestionDetailDto getQuestionDetailDto(Long id, int answerPage, String sort) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if(questionOpt.isEmpty()){
            throw new DataNotFoundException(QUESTION_NOT_FOUND);
        }
        return createQuestionDetailDto(questionOpt.get(),
                                        answerPage,
                                        ANSWER_PAGE_SIZE,
                                        getComparator(sort));
    }

    /**
     *  id 를 통해 질문을 조회하여 반환
     */
    public Question getQuestionById(Long id) {
        Optional<Question> byId = questionRepository.findById(id);
        if(byId.isEmpty()){
            throw new DataNotFoundException(QUESTION_NOT_FOUND);
        }
        return byId.get();
    }

    public void create(String subject, String content, String categoryName, String username) {

        Author author = authorService.findByUsername(username);
        if(categoryName == null){
            categoryName = "전체";
        }
        Category category = categoryService.findByName(categoryName);
        questionRepository.save(Question.createQuestion(subject, content, category, author));
    }

    public void modify(Question question, String subject, String content, String categoryName) {
        modifyQuestion(question, subject, content , categoryService.findByName(categoryName));
        questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }

    public Question getQuestion(Long id) {
        Optional<Question> byId = questionRepository.findById(id);
        if(byId.isEmpty()){
            throw new DataNotFoundException(QUESTION_NOT_FOUND);
        }
        return byId.get();
    }

    public void vote(Question question, Author author) {
        question.getVoter().add(author);
        questionRepository.save(question);
    }

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true); // 중 복 을 제 거
                Join<Question, Author> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, Author> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"),     // 제 목
                        cb.like(q.get("content"), "%" + kw + "%"),          // 내 용
                        cb.like(u1.get("username"), "%" + kw + "%"),        // 질 문 작 성자
                        cb.like(a.get("content"), "%" + kw + "%"),          // 답 변 내 용
                        cb.like(u2.get("username"), "%" + kw + "%"));       // 답 변 작 성자
            }
        };
    }

    private Comparator<Answer> getComparator(String sort) {
        return SORT_LATEST.equals(sort)
                ? comparing(Answer::getCreateDate).reversed()
                : comparingInt(a -> -a.getVoter().size());
    }

    public void updateViews(Long id) {
        questionRepository.findById(id).ifPresent(question -> {
            question.incemenetViews();
            questionRepository.save(question);
        });
    }

    public List<QuestionProfileDto> getQuestionProfileDtoList(String name) {
        return questionRepository.findByAuthor(authorService.findByUsername(name)).stream()
                .map(QuestionProfileDto::createQuestionProfileDto)
                .toList();
    }
}
