package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
