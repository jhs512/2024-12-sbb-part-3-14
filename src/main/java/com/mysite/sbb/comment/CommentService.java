package com.mysite.sbb.comment;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public void createComment(String content, Question question, SiteUser user) {
        Comment c = new Comment();
        c.setContent(content);
        c.setAuthor(user);
        c.setQuestion(question);
        commentRepository.save(c);
    }

    public void createComment(String content, Answer answer, SiteUser user) {
        Comment c = new Comment();
        c.setContent(content);
        c.setAuthor(user);
        c.setAnswer(answer);
        commentRepository.save(c);
    }

    public Comment getComment(Integer id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            return commentOptional.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
}
