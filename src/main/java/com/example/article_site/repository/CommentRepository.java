package com.example.article_site.repository;

import com.example.article_site.domain.Author;
import com.example.article_site.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAuthor(Author byUsername);
}
