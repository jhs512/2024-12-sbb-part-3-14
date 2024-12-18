package org.example.jtsb02.question.repository;

import org.example.jtsb02.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
