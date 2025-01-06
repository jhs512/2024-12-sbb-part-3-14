package org.example.jtsb02.comment.repository;

import org.example.jtsb02.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByAuthorId(Long authorId, Pageable pageable);
}
