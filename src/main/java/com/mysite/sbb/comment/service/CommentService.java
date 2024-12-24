package com.mysite.sbb.comment.service;

import com.mysite.sbb.comment.repository.CommentRepository;
import com.mysite.sbb.entity.Answer;
import com.mysite.sbb.entity.Comment;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment createComment(Answer answer, SiteUser author, String content) {
        Comment comment = new Comment();
        comment.setAnswer(answer);
        comment.setAuthor(author);
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        this.commentRepository.save(comment);

        return comment;
    }

    public Comment createReply(Comment parent, SiteUser author, String content) {
        Comment comment = new Comment();
        comment.setParent(parent);
        comment.setAnswer(parent.getAnswer());
        comment.setAuthor(author);
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        this.commentRepository.save(comment);

        return comment;
    }

    public Comment getComment(Integer commentId) {
        Optional<Comment> comment = this.commentRepository.findById(commentId);

        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("No such comment");
        }
    }
}
