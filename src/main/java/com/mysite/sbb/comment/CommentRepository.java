package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByQuestion(Question question);

}
