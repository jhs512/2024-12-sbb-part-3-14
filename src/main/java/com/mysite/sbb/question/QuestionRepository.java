package com.mysite.sbb.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String Content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAllByAuthor_Username(String username, Pageable pageable);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);

    @Query("select "
            + "distinct q "
            + "from Question q "
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Answer a on a.question=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "left outer join Category c on q.category=c "
            + "where "
            + "c.id = :categoryId and ("
            + "q.subject like %:kw% "
            + "or q.content like %:kw% "
            + "or u1.username like %:kw% "
            + "or a.content like %:kw% "
            + "or u2.username like %:kw% )")
    Page<Question> findAllByKeywordAndCategory(@Param("kw") String kw, @Param("categoryId")int categoryId, Pageable pageable);
}
