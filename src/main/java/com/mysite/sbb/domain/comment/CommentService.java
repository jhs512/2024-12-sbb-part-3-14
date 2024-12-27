package com.mysite.sbb.domain.comment;


import com.mysite.sbb.domain.answer.Answer;
import com.mysite.sbb.domain.answer.AnswerRepository;
import com.mysite.sbb.domain.question.Question;
import com.mysite.sbb.domain.question.QuestionRepository;
import com.mysite.sbb.domain.user.SiteUser;
import com.mysite.sbb.web.comment.dto.request.CommentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public Comment addCommentToQuestion(CommentRequestDTO dto, SiteUser author) {
        Question question = questionRepository.findById(dto.targetId());
        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setAuthor(author);
        comment.setQuestion(question);

        System.out.println("Author ID: " + author.getId());
        System.out.println("Author Username: " + author.getUsername());

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment addCommentToAnswer(CommentRequestDTO dto, SiteUser author) {
        Answer answer = answerRepository.findById(dto.targetId());
        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setAuthor(author);
        comment.setAnswer(answer);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsForQuestion(Long questionId) {
        return commentRepository.findByQuestionId(questionId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsForAnswer(Long answerId) {
        return commentRepository.findByAnswerId(answerId);
    }
}
