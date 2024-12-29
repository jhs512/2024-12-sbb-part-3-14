package com.mysite.sbb.domain.comment.service;


import com.mysite.sbb.domain.answer.doamin.Answer;
import com.mysite.sbb.domain.answer.repository.AnswerRepository;
import com.mysite.sbb.domain.comment.domain.Comment;
import com.mysite.sbb.domain.comment.repository.CommentRepository;
import com.mysite.sbb.domain.question.domain.Question;
import com.mysite.sbb.domain.question.repository.QuestionRepository;
import com.mysite.sbb.domain.user.domain.SiteUser;
import com.mysite.sbb.web.api.v1.comment.dto.request.CommentRequestDTO;
import com.mysite.sbb.web.api.v1.comment.dto.request.CommentTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Override
    public Comment addComment(CommentRequestDTO dto, SiteUser author, Long questionId) {
        Comment comment = createBaseComment(dto, author, questionId);
        attachTarget(dto, comment);
        return commentRepository.save(comment);
    }

    private Comment createBaseComment(CommentRequestDTO dto, SiteUser author, Long questionId) {
        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setAuthor(author);
        comment.setQuestion(findQuestionById(questionId));
        return comment;
    }

    private void attachTarget(CommentRequestDTO dto, Comment comment) {
        if (dto.targetType() == CommentTargetType.ANSWER) {
            comment.setAnswer(findAnswerById(dto.targetId()));
        } else if (dto.targetType() != CommentTargetType.QUESTION) {
            throw new IllegalStateException("잘못된 대상 유형입니다 : " + dto.targetType());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> getCommentsForQuestion(int questionId) {
        return commentRepository.findByQuestion_IdAndAnswerIsNull(questionId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> getCommentsForAnswer(int answerId) {
        return commentRepository.findByAnswer_Id(answerId);
    }

    private Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found for id: " + questionId));
    }

    private Answer findAnswerById(Long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found for id: " + answerId));
    }
}
