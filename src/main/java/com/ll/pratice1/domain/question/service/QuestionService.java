package com.ll.pratice1.domain.question.service;

import com.ll.pratice1.DataNotFoundException;
import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.answer.repository.AnswerRepository;
import com.ll.pratice1.domain.category.Category;
import com.ll.pratice1.domain.category.repository.CategoryRepository;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.question.repository.QuestionRepository;
import com.ll.pratice1.domain.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;

    public Page<Question> getList(int page, String kw, String category) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Category byCategory = categoryRepository.findByCategory(category);
        Specification<Question> spec = search(kw, byCategory);

        return this.questionRepository.findAll(spec, pageable);
    }

    public Page<Answer> getAnswerList(Question question, int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        Pageable pageable = null;
        if (sort.equals("latest")){
            sorts.add(Sort.Order.desc("createDate"));
            pageable = PageRequest.of(page, 5, Sort.by(sorts));
        }else if(sort.equals("vote")) {
            sorts.add(Sort.Order.desc("voter"));
            pageable = PageRequest.of(page, 5, Sort.by(sorts));
        }else{
            sorts.add(Sort.Order.desc("createDate"));
            pageable = PageRequest.of(page, 5, Sort.by(sorts));
        }
        // 페이징된 Answer 리스트 반환
        return answerRepository.findByQuestion(question, pageable);
    }

    public Question getQuestion(int id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, Category category, SiteUser author) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setCategory(category);
        question.setAuthor(author);
        this.questionRepository.save(question);
    }

    public void delete(Question question){
        this.questionRepository.delete(question);
    }


    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void vote(Question question, SiteUser siteUser){
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String kw, Category category) {
        return new Specification<Question>() {
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = q.join("author", JoinType.LEFT);

                Predicate keywordPredicate = cb.or(
                        cb.like(q.get("subject"), "%" + kw + "%"),
                        cb.like(q.get("content"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"),
                        cb.like(a.get("content"), "%" + kw + "%"),
                        cb.like(u2.get("username"), "%" + kw + "%")
                );

                // 카테고리 조건 추가
                if (category != null) {
                    return cb.and(keywordPredicate, cb.equal(q.get("category"), category));
                } else {
                    return keywordPredicate;
                }
            }
        };
    }


}
