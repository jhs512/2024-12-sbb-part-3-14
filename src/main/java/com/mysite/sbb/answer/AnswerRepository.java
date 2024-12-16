package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findAllByQuestionOrderByCreateDateDesc(Question question, Pageable pageable);

    @Query("SELECT a FROM Answer a "
            + "LEFT JOIN a.voter as v "
            + "WHERE a.question=:question "
            + "GROUP BY a.id "
            + "ORDER BY count(v) desc")
    Page<Answer> findAllWithVoterCountDesc(Question question, Pageable pageable);
}
