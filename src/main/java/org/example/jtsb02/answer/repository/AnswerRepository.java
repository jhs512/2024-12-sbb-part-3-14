package org.example.jtsb02.answer.repository;

import org.example.jtsb02.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
