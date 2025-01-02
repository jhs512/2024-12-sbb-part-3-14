package com.ll.pratice1.domain.comment.service;

import com.ll.pratice1.DataNotFoundException;
import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.comment.Comment;
import com.ll.pratice1.domain.comment.repository.CommentRepository;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public void create(Object object, String content, SiteUser siteUser) {
        Comment comment = new Comment();
        comment.setCreateDate(LocalDateTime.now());
        comment.setComment(content);
        comment.setSiteUser(siteUser);
        if (object instanceof Question) {
            comment.setQuestion((Question) object);
        } else if (object instanceof Answer) {
            comment.setAnswer((Answer) object);
        }
        this.commentRepository.save(comment);

    }

    public Comment getComment(Question question) {
        Optional<Comment> comment = this.commentRepository.findByQuestion(question);
        if(comment.isPresent()){
            return comment.get();
        }else{
            throw new DataNotFoundException("answer not found");
        }
    }

    public List<Comment> getCommentList(SiteUser siteUser) {
        List<Comment> commentList = this.commentRepository.findBySiteUser(siteUser);
        return commentList;
    }

}

