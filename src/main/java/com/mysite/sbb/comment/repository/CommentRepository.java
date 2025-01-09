package com.mysite.sbb.comment.repository;

import com.mysite.sbb.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
