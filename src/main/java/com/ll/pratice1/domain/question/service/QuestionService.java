package com.ll.pratice1.domain.question.service;

import com.ll.pratice1.DataNotFoundException;
import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.category.Category;
import com.ll.pratice1.domain.category.repository.CategoryRepository;
import com.ll.pratice1.domain.comment.Comment;
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
    private final CategoryRepository categoryRepository;

    public Page<Question> getList(int page, String kw, String category, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        // 동적으로 정렬 기준 추가
        if ("lastAnswer".equals(sort)) {
            sorts.add(Sort.Order.desc("recentAnswerDate")); // 최근 답변 순
        } else if ("lastComment".equals(sort)) {
            sorts.add(Sort.Order.desc("recentCommentDate"));// 최근 댓글 순
        } else {
            sorts.add(Sort.Order.desc("createDate")); // 기본 정렬
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Category byCategory = categoryRepository.findByCategory(category);
        Specification<Question> spec = search(kw, byCategory, sort);
        return this.questionRepository.findAll(spec, pageable);
    }


    public Question getQuestion(int id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public List<Question> getList(SiteUser siteUser) {
        List<Question> questionList = this.questionRepository.findByAuthor(siteUser);
        return questionList;
//        if (question.isPresent()) {
//            return question.get();
//        } else {
//            throw new DataNotFoundException("question not found");
//        }
    }

    public void recentDate(Question question, Answer answer, Comment comment){
        // Answer 저장 시
        question.setRecentAnswerDate(answer.getCreateDate());
        questionRepository.save(question);

        // Comment 저장 시
        question.setRecentCommentDate(comment.getCreateDate());
        questionRepository.save(question);
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

    private Specification<Question> search(String kw, Category category, String sort) {
        return new Specification<Question>() {
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = q.join("author", JoinType.LEFT);
                Join<Question, Comment> c = q.join("commentList", JoinType.LEFT);


                Predicate keywordPredicate = cb.or(
                        cb.like(q.get("subject"), "%" + kw + "%"),
                        cb.like(q.get("content"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"),
                        cb.like(a.get("content"), "%" + kw + "%"),
                        cb.like(u2.get("username"), "%" + kw + "%"),
                        cb.like(c.get("comment"), "%" + kw + "%")
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
