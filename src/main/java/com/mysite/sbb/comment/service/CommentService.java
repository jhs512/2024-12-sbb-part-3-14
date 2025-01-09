package com.mysite.sbb.comment.service;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.comment.entity.Commentable;
import com.mysite.sbb.comment.repository.CommentRepository;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment write(Commentable commentable, SiteUser author, String content) {
        Comment comment = Comment.builder()
                .content(content)
                .author(author)
                .build();

        if (commentable instanceof Question) {
            comment.setQuestion((Question) commentable);
        } else if (commentable instanceof Answer) {
            Answer answer = (Answer) commentable;
            comment.setQuestion(answer.getQuestion());
            comment.setAnswer(answer);
        } else {
            throw new IllegalArgumentException("Unsupported target type: " + commentable.getClass().getName());
        }

        commentRepository.save(comment);
        return comment;
    }
}
