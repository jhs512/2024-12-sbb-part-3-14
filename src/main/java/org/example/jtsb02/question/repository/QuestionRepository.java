package org.example.jtsb02.question.repository;

import org.example.jtsb02.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAll(Pageable pageable);
    @Query("select "
        + "distinct q "
        + "from Question q "
        + "left outer join Member m1 on q.author=m1 "
        + "left outer join Answer a on a.question=q "
        + "left outer join Member m2 on a.author=m2 "
        + "where "
        + "   q.subject like %:kw% "
        + "   or q.content like %:kw% "
        + "   or m1.nickname like %:kw% "
        + "   or a.content like %:kw% "
        + "   or m2.nickname like %:kw% ")
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
    Page<Question> findByAuthorId(Long authorId, Pageable pageable);
}