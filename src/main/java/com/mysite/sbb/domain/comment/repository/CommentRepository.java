package com.mysite.sbb.domain.comment.repository;

import com.mysite.sbb.domain.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByQuestion_IdAndAnswerIsNull(int question_id);
    List<Comment> findByAnswer_Id(int answerId);

    List<Comment> findTop5ByAuthorUsernameOrderByCreateDateDesc(String username);

    Page<Comment> findAll(Specification<Comment> spec, Pageable pageable);
}
