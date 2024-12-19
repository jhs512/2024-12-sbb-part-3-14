package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public List<Question> getList() {
        return this.questionRepository.findAll();
    }

    public Question getQuestion(int id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if(question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser author, Category category) {
        Question question = Question.builder()
                .subject(subject)
                .content(content)
                .author(author)
                .category(category)
                .build();
        questionRepository.save(question);
    }

    public Page<Question> getList(int page, String kw, int categoryId) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        // Specification<Question> spec = search(kw);
        // return this.questionRepository.findAll(spec, pageable);
        return this.questionRepository.findAllByKeywordAndCategory(kw, categoryId, pageable);
    }

    public void modify(Question question, String subject, String content, Category category) {
        question.setSubject(subject);
        question.setContent(content);
        question.setCategory(category);
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    public Page<Question> getMyQuestionList(String username, int page) {
        int itemsPerPage = 10;
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, itemsPerPage, Sort.by(sorts));
        return this.questionRepository.findAllByAuthor_Username(username, pageable);
    }

    public void incrementViewCount(int questionId, String ip) {

        String redisKey = "view:" + questionId + ":ip:" + ip;
        Boolean hasViewed = redisTemplate.hasKey(redisKey);

        if(hasViewed == null || !hasViewed) {
            redisTemplate.opsForValue().set(redisKey, "1", 24, TimeUnit.HOURS);

            Question question = getQuestion(questionId);
            question.setViewCount(question.getViewCount() + 1);
            questionRepository.save(question);
        }
    }

    /*
    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%"+kw + "%"),
                        cb.like(q.get("content"), "%"+kw+"%"),
                        cb.like(u1.get("username"), "%"+kw+"%"),
                        cb.like(a.get("content"), "%"+kw+"%"),
                        cb.like(u2.get("username"), "%"+kw+"%"));
            }
        };
    }
     */
}
