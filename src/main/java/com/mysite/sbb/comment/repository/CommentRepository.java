package com.mysite.sbb.comment.repository;

import com.mysite.sbb.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
