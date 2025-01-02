package com.mysite.sbb.domain.answer.repository;

import com.mysite.sbb.domain.answer.domain.Answer;
import com.mysite.sbb.domain.question.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findByQuestion(Question question, Pageable pageable);
    
    Optional<Answer> findById(Long id);

    List<Answer> findTop5ByAuthorUsernameOrderByCreateDateDesc(String username);

    Page<Answer> findAll(Specification<Answer> spec, Pageable pageable);

}
