package com.mysite.sbb.answer.repository;

import com.mysite.sbb.entity.Answer;
import com.mysite.sbb.entity.Question;
import com.mysite.sbb.entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    //  댓글에 where 조건 없이, 모든 댓글을 가져오는 경우
    Page<Answer> findAll(Pageable pageable);

    //  댓글 id 별로 댓글을 가져오는 경우
    Page<Answer> findByQuestionId(Integer id, Pageable pageable);

    //  Paging 및 추천수별 정렬 기능
    @Query(name = "Answer.findByQuestionIdOrderByVoterCountDesc")
    Page<Answer> findByQuestionIdOrderByVoterCountDesc(@Param("questionId") Integer questionId, Pageable pageable);

    Page<Answer> findByAuthor(SiteUser siteUser, Pageable pageable);

    List<Answer> findTop5ByOrderByCreateDateDesc();
}
