package com.site.sss.question;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    //프록시 객체가 메서드 호출 가로채 퀴리 실행
    //메서드 분석은 리플렉션 이용
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject,String content);

    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> spec,Pageable pageable);
}
