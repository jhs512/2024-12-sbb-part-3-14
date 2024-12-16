package com.example.article_site.repository;

import com.example.article_site.domain.Author;
import com.example.article_site.domain.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAuthor(Author byUsername);

    @EntityGraph(attributePaths = {"author"})
    List<Comment> findAll();    // 모든 댓글을 작성자 정보와 함께 가져옴

    @EntityGraph(attributePaths = {"author"})
    List<Comment> findByAnswerId(Long answerId);    // 특정 답변의 댓글만 작성자 정보와 함께 가져옴
}
