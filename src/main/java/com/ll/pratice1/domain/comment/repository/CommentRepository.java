package com.ll.pratice1.domain.comment.repository;

import com.ll.pratice1.domain.comment.Comment;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Optional<Comment> findByQuestion(Question question);

    List<Comment> findBySiteUser(SiteUser siteUser);
}
