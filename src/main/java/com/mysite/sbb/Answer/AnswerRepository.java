package com.mysite.sbb.Answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    @Query("select distinct a from Answer a " +
            "left join a.author u " +
            "where a.content like %:kw% or u.username like %:kw%")
    Page<Answer> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

    List<Answer> findByAuthor_Username(String username);

    // 최근 답변 조회 (최신순으로 10개)
    @Query("SELECT a FROM Answer a ORDER BY a.createDate DESC")
    List<Answer> findRecentAnswers(Pageable pageable);

}
