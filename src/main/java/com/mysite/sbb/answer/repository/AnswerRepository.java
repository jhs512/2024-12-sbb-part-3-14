package com.mysite.sbb.answer.repository;

import com.mysite.sbb.answer.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Page<Answer> findByQuestionId(long id, Pageable pageable);
}
