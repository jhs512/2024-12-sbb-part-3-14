package com.mysite.sbb.comment.repository;

import com.mysite.sbb.entity.Comment;
import com.mysite.sbb.entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByAuthor(SiteUser siteUser, Pageable pageable);
}
