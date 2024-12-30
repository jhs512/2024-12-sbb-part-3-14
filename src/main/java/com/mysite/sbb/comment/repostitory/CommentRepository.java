package com.mysite.sbb.comment.repostitory;

import com.mysite.sbb.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByQuestionId(Integer questionId);

    List<Comment> findAllByAuthorId(Integer id);
}
