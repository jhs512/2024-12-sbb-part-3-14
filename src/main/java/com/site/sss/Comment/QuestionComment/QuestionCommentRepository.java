package com.site.sss.Comment.QuestionComment;

import com.site.sss.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionCommentRepository extends JpaRepository<QuestionComment,Integer> {

    List<QuestionComment> findByQuestion(Question question);
}
