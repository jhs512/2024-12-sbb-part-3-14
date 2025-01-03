package com.mysite.sbb.Comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByAuthor_Username(String username);

    // 최근 댓글 조회 (최신순으로 10개)
    @Query("SELECT c FROM Comment c ORDER BY c.createDate DESC")
    List<Comment> findRecentComments(Pageable pageable);
}