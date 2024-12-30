package com.mysite.sbb.domain.answer.service;


import com.mysite.sbb.domain.answer.doamin.Answer;
import com.mysite.sbb.domain.answer.repository.AnswerRepository;
import com.mysite.sbb.domain.question.domain.Question;
import com.mysite.sbb.domain.user.domain.SiteUser;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.web.api.v1.answer.dto.response.AnswerListResponseDTO;
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

@RequiredArgsConstructor
@Service
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<AnswerListResponseDTO> getList(int page, String kw) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        Specification<Answer> spec = search(kw);
        return answerRepository.findAll(spec, pageable)
                .map(AnswerListResponseDTO::new);
    }

    public Specification<Answer> search(String kw) {
        return (Root<Answer> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);

            // Answer와 Question을 Join
            Join<Answer, Question> questionJoin = root.join("question", JoinType.LEFT);

            // Answer의 작성자 Join
            Join<Answer, SiteUser> authorJoin = root.join("author", JoinType.LEFT);

            // 검색 조건 설정
            return cb.or(
                    // Answer의 내용 검색
                    cb.like(root.get("content"), "%" + kw + "%"),

                    // Question의 제목 검색
                    cb.like(questionJoin.get("subject"), "%" + kw + "%"),

                    // Answer 작성자 검색
                    cb.like(authorJoin.get("username"), "%" + kw + "%")
            );
        };
    }


    @Override
    public Answer create(Question question, String content, SiteUser author) {
        return createAnswer(question, content, author);
    }

    private Answer createAnswer(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setQuestion(question);
        answer.setAuthor(author);
        return answerRepository.save(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public Answer getAnswer(Integer id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Answer not found with id: " + id));
    }

    @Override
    public void modify(Answer answer, String content) {
        answer.setContent(content);
        this.answerRepository.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    @Override
    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }
}
