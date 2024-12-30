package com.mysite.sbb.user.entity;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.question.entity.Question;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserPostsDTO {
    private final List<Question> questionList;
    private final List<Answer> answerList;
    private final List<Comment> commentList;
}
