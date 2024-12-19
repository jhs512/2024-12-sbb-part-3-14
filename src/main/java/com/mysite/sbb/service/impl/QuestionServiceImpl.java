package com.mysite.sbb.service.impl;

import com.mysite.sbb.dto.QuestionDetailDTO;
import com.mysite.sbb.dto.QuestionListDTO;
import com.mysite.sbb.exception.DataNotFoundException;
import com.mysite.sbb.domain.Answer;
import com.mysite.sbb.domain.Question;
import com.mysite.sbb.domain.SiteUser;
import com.mysite.sbb.repository.QuestionRepository;
import com.mysite.sbb.service.QuestionService;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public List<QuestionListDTO> getAllQuestions() {

        List<Question> all = questionRepository.findAll();

        return all.stream()
                .map(QuestionListDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<QuestionListDTO> getList(int page, String kw) {
        // 1. 정렬 조건 설정
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));

        // 2. 검색 조건 설정
        Specification<Question> spec = search(kw);

        // 3. 엔티티 -> DTO 변환
        Page<Question> questions = questionRepository.findAll(spec, pageable);// 검색 및 페이징 결과
        return questions.map(QuestionListDTO::new);
    }

    @Override
    public QuestionDetailDTO getQuestionDetail(Integer id) {
        Optional<Question> question = questionRepository.findById(id);
        if(question.isPresent()) {
            return new QuestionDetailDTO(question.get(), question.get().getAnswerList());
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    @Override
    public Question getQuestion(Integer id) {
        Optional<Question> question = questionRepository.findById(id);
        if(question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    @Override
    public void create(String subject, String content, SiteUser user) {
        Question q= new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        questionRepository.save(q);
    }

    @Override
    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        questionRepository.save(question);
    }

    @Override
    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    @Override
    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    @Override
    public Specification<Question> search(String kw){
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
}
