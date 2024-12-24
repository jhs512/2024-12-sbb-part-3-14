package com.mysite.sbb.answer.repository;

import com.mysite.sbb.answer.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findByQuestionId(Integer questionId, Pageable pageable);

    Page<Answer> findByQuestionIdOrderByVoterDesc(Integer id, Pageable pageable);

    Page<Answer> findByQuestionIdOrderByCreateDateDesc(Integer id, Pageable pageable);
}
