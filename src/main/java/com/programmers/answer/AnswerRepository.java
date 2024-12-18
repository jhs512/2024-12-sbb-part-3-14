package com.programmers.answer;

import com.programmers.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    long countByQuestion(Question question);

    Page<Answer> findByQuestion(Question question, Pageable pageable);
}
