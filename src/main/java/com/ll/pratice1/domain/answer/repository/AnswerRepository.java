package com.ll.pratice1.domain.answer.repository;

import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findByQuestion(Question question, Pageable pageable);

    List<Answer> findByAuthor(SiteUser siteUser);
}
