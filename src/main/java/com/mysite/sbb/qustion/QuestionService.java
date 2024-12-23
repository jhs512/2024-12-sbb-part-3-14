package com.mysite.sbb.qustion;
import java.util.*;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.catrgory.Category;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import com.mysite.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getList(){
        return this.questionRepository.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }
    public Question getCurrentQuestion(){
        return questionRepository.findTopByOrderByCreateDateDesc();
    }

    public void create(SiteUser siteUser, String subject, String content,Category category) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setUser(siteUser);
        question.setCategory(category);
        question.setView(0);
        this.questionRepository.save(question);
    }

    public Page<Question> getList(int page){
        Pageable pageable = PageRequest.of(page,10);
        return this.questionRepository.findAllByOrderByIdDesc(pageable);
    }
    public Page<Question> getList(int page, String kw,Category category) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw,category);
        return this.questionRepository.findAll(spec, pageable);
    }

    public void delete(int id){
        this.questionRepository.deleteById(id);
    }
    public void vote(Question question,SiteUser siteUser){
        question.getVoterSet().add(siteUser);
        this.questionRepository.save(question);
    }
    //1.userlist가져와서 자바에서 탐색
    //2.user객체 가져온다음 db에서 호출
    public Question modify(int id, String subject, String content){
            Question question = getQuestion(id);
            question.setSubject(content);
            question.setContent(subject);
            return question;
    }
    public void setView(Question question){
        question.setView(question.getView()+1);
    }
    public List<Answer> best(int id){
        Question question = this.getQuestion(id);
        List<Answer> answers = question.getAnswerList();
        return  answers.stream().filter(q -> !q.getVoterSet().isEmpty())
                .sorted(Comparator.comparing(Answer::getVoterCount).reversed()).limit(3)
                .toList();
    }

    private Specification<Question> search(String kw, Category category) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("user", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("user", JoinType.LEFT);
                return  cb.and(cb.equal(q.get("category") ,category),
                        cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                                cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                                cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                                cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                                cb.like(u2.get("username"), "%" + kw + "%")) // 답변 작성자
                        );

            }
        };
    }

}
