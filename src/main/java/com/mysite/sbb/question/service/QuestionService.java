package com.mysite.sbb.question.service;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.category.entity.Category;
import com.mysite.sbb.category.repository.CategoryRepository;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.user.entity.SiteUser;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    private Specification<Question> search(String kw) {
        return new Specification<Question>() {
            private static final long serialVersionUTD = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answers", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);

                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"),
                        cb.like(q.get("content"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"),
                        cb.like(a.get("content"), "%" + kw + "%"),
                        cb.like(u2.get("username"), "%" + kw + "%"));
            }
        };
    }

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    public Question findById(long id) {
        Optional<Question> op = questionRepository.findById(id);

        if (op.isPresent()) return op.get();
        else throw new RuntimeException("데이터가 없습니다.");
    }

    public Question write(String subject, String content, String categoryId, SiteUser author) {
        Category category = categoryRepository.findById(Long.parseLong(categoryId)).get();

        Question question = Question.builder()
                .subject(subject)
                .content(content)
                .author(author)
                .category(category)
                .build();
        return questionRepository.save(question);
    }

    public Page<Question> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable);
    }

    public Page<Question> getList(int page, String kw, String filter) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc(getSort(filter))));
        //Specification<Question> spec = search(kw);
        return questionRepository.findAllByKeyword(kw, pageable);
    }

    private String getSort(String filter) {
        if ("answer".equals(filter)) {
            return "a.modifiedDate";
        } else if ("comment".equals(filter)) {
            return "c.modifiedDate";
        }

        return "createdDate";
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);

        questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser author) {
        question.getVoter().add(author);
        questionRepository.save(question);
    }
}
