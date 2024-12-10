package com.mysite.sbb.qustion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
}
