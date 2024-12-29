package com.mysite.sbb.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByQuestion_IdAndAnswerIsNull(int question_id);
    List<Comment> findByAnswer_Id(int answerId);

    List<Comment> findTop5ByAuthorUsernameOrderByCreateDateDesc(String username);
}
