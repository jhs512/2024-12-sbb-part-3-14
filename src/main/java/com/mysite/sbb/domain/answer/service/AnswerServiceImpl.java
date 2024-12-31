package com.mysite.sbb.domain.answer.service;


import com.mysite.sbb.domain.answer.domain.Answer;
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

@RequiredArgsConstructor
@Service
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
            Join<Answer, Question> questionJoin = root.join("question", JoinType.LEFT);
            Join<Answer, SiteUser> authorJoin = root.join("author", JoinType.LEFT);

            // 검색 조건 설정
            return cb.or(
                    cb.like(root.get("content"), "%" + kw + "%"),
                    cb.like(questionJoin.get("subject"), "%" + kw + "%"),
                    cb.like(authorJoin.get("username"), "%" + kw + "%")
            );
        };
    }

    @Override
    @Transactional
    public Answer create(Question question, String content, SiteUser author) {
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
    @Transactional
    public void modify(Answer answer, String content) {
        answer.setContent(content);
        this.answerRepository.save(answer);
    }

    @Override
    @Transactional
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    @Override
    @Transactional
    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }
}
