package com.mysite.sbb.question.service;

import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.category.repository.CategoryRepository;
import com.mysite.sbb.comment.repository.CommentRepository;
import com.mysite.sbb.entity.*;
import com.mysite.sbb.exception.DataNotFoundException;
import com.mysite.sbb.question.repository.QuestionRepository;
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
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    public List<Question> getList() {
        return this.questionRepository.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if(question.isPresent()) {
            Question q = question.get();
            q.setViewCount(q.getViewCount() + 1);
            this.questionRepository.save(q);
            return q;
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, String categoryName, boolean isExistingCategory, SiteUser user) {
        Category category;

        if (isExistingCategory) {
            // 기존 카테고리 재사용
            category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new IllegalArgumentException("선택된 카테고리가 존재하지 않습니다."));
        } else {
            // 새 카테고리 생성 로직
            if (categoryName == null || categoryName.trim().isEmpty()) {
                throw new IllegalArgumentException("카테고리 이름을 입력해주세요.");
            }

            List<Category> similarCategories = categoryRepository.findSimilarCategories(categoryName);
            if (!similarCategories.isEmpty()) {
                throw new IllegalArgumentException("이미 존재하는 카테고리입니다: " + similarCategories.get(0).getName());
            }

            category = new Category();
            category.setName(categoryName);
            categoryRepository.save(category);
        }

        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCategory(category);  // 카테고리 설정
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(user);
        this.questionRepository.save(question);
    }

    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.questionRepository.findAllByKeyword(kw, pageable);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public  void delete(Question question) {

        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String kw) {
        return new Specification<Question>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true); // 중복제거
                Join<Question,SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer,SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                       cb.like(q.get("content"), "%" + kw + "%"), // 내용
                       cb.like(u1.get("username"), "%" + kw + "%"),
                       cb.like(a.get("content"), "%" + kw + "%"),
                       cb.like(u2.get("username"), "%" + kw + "%")
                );
            }
        };
    }

    public List<Answer> getRecentAnswers() {
        return this.answerRepository.findTop5ByOrderByCreateDateDesc();
    }

    public List<Comment> getRecentComments() {
        return this.commentRepository.findTop5ByOrderByCreateDateDesc();
    }
}
