package com.site.sss.question;

import com.site.sss.Category.Category;
import com.site.sss.DataNotFoundException;
import com.site.sss.answer.Answer;
import com.site.sss.user.SiteUser;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
//롬북 코드 추가는 컴파일 바이트코드 레벨에서 진행됨 ast생성시 같이 생성되어 삽입
//빈 같은 스프링은 실행 시점에 객체 주입. 주입받는 객체를 매개변수로 가지는 생성자를 통해 주입
//롬북은 필요시 사용, 코드 줄일수 있 하지만 명시적으로 코드 보고 싶음 사용 x

@RequiredArgsConstructor
@Service
public class QuestionService {


    private final QuestionRepository questionRepository;

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser user, Category category) {
        Question q=new Question();
        q.setContent(content);
        q.setSubject(subject);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        q.setCategory(category);
        q.setViews(0);


        this.questionRepository.save(q);
    }

    public Page<Question> getList(int page,String kw,Category category) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));

        Specification<Question> spec=search(kw)
                .and(categoryEquals(category));
        return this.questionRepository.findAll(spec,pageable);
    }

    private Specification<Question> categoryEquals(Category category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null) {
                return null; // categoryId가 없으면 조건 추가 안함
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void deleteAll() {
        this.questionRepository.deleteAll();
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    public void upViews(Question question) {
        int views=question.getViews();
        question.setViews(views+1);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }


        };
    }
}
