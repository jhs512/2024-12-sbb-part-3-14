package com.programmers.answer;

import com.programmers.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    long countByQuestion(Question question);
}
