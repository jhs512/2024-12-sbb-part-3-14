package com.mysite.sbb.answer;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    Page<Answer> findAll(Specification<Answer> spec, Pageable pageable);
    Page<Answer> findByQuestion(Question question, Pageable pageable);
    Page<Answer> findByAuthor(SiteUser siteUser, Pageable pageable);
    @Query("select  a"
            + " from Answer a "
            + " left outer join SiteUser u on a.author=u "
            + " where u.username = :username "
            + "order by a.createDate desc")
    List<Answer> findQuestionByAuthor(@Param("username") String username, Pageable pageable);

    @Query("select a "
            + " from Answer a "
            + "order by a.createDate desc")
    Page<Answer> findAllByDesc(Pageable pageable);
}
