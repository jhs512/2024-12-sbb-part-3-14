package com.mysite.sbb.comment.service;

import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.comment.repostitory.CommentRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void createComment(Integer questionId, String content, Integer parentId, SiteUser author) {
        Comment comment = new Comment();
        comment.setCreateDate(LocalDateTime.now());
        comment.setContent(content);
        comment.setQuestionId(questionId);
        comment.setParentId(parentId);
        comment.setAuthor(author);
        this.commentRepository.save(comment);
    }

    public List<Comment> findAll(Integer questionId) {
        return this.commentRepository.findByQuestionId(questionId);
    }

    public Comment findComment(Integer commentId) {
        Optional<Comment> commentOptional = this.commentRepository.findById(commentId);
        if(commentOptional.isPresent()) {
            return commentOptional.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public void modifyComment(Comment comment, String content) {
        comment.setContent(content);
        this.commentRepository.save(comment);
    }

    public void deleteComment(Comment comment) {
        this.commentRepository.delete(comment);
    }
}
