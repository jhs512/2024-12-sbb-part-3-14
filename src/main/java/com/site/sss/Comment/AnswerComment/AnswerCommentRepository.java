package com.site.sss.Comment.AnswerComment;

import com.site.sss.answer.Answer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerCommentRepository  extends JpaRepository<AnswerComment,Integer> {
    List<AnswerComment> findByAnswer(Answer answer);

}
