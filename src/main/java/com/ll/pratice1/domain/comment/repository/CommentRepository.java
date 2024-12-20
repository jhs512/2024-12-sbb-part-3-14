package com.ll.pratice1.domain.comment.repository;

import com.ll.pratice1.domain.comment.Comment;
import com.ll.pratice1.domain.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Optional<Comment> findByQuestion(Question question);
}
