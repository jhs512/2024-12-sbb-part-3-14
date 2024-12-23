package com.mysite.sbb.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllByAuthor_Username(String username, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.answer.id = :answerId ORDER BY SIZE(c.voter) DESC")
    Page<Comment> findCommentByAnswerIdOrderByVoterCountDesc(@Param("answerId") Integer answerId, Pageable pageable);
}
