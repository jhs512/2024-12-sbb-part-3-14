package com.kkd.sbb.answer;

import com.kkd.sbb.DataNotFoundException;
import com.kkd.sbb.question.Question;
import com.kkd.sbb.user.SiteUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.answerRepository.save(answer);
        return answer;
    }

    public Answer getAnswer(Integer id){
        Optional<Answer> answer = this.answerRepository.findById(id);

        if(answer.isPresent()){
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer){
        this.answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser siteUser) {
        if (answer.getVoter().contains(siteUser)) {
            // 이미 추천한 사용자라면 추천 취소
            answer.getVoter().remove(siteUser);
        } else {
            // 추천하지 않은 사용자라면 추천 추가
            answer.getVoter().add(siteUser);
        }
        this.answerRepository.save(answer);
    }

    public Page<Answer> getAnswerList(Question question, int page, String answerOrderMethod) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (answerOrderMethod.startsWith("recommend")) {
            sorts.add(Sort.Order.desc("voter"));
        }
        else if(answerOrderMethod.startsWith("time")){
            sorts.add(Sort.Order.desc("createDate"));
        }
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return this.answerRepository.findByQuestion(question, pageable);
    }

    public Page<Answer> getListByAuthor(int page, SiteUser siteUser) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 3, Sort.by(sorts));

        return this.answerRepository.findByAuthor(siteUser, pageable);
    }

    public Specification<Answer> hasVoter(SiteUser siteUser) {
        return new Specification<Answer>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Answer> a, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                return cb.isMember(siteUser, a.get("voter"));
            }
        };
    }

    public Page<Answer> getListByVoter(int page, SiteUser siteUser) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 3, Sort.by(sorts));
        Specification<Answer> spec = this.hasVoter(siteUser);

        return this.answerRepository.findAll(spec, pageable);
    }
}
