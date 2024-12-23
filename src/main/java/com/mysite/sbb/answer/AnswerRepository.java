package com.mysite.sbb.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId ORDER BY SIZE(a.voter) DESC")
    Page<Answer> findAnswerByQuestionIdOrderByVoterCountDesc(@Param("questionId") Integer questionId, Pageable pageable);
    Page<Answer> findAllByAuthor_Username(String username, Pageable pageable);
    List<Answer> findAllByQuestion_Id(int questionId);
}
