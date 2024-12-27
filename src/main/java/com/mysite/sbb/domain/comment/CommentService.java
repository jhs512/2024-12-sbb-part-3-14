package com.mysite.sbb.domain.comment;


import com.mysite.sbb.domain.answer.Answer;
import com.mysite.sbb.domain.answer.AnswerRepository;
import com.mysite.sbb.domain.question.Question;
import com.mysite.sbb.domain.question.QuestionRepository;
import com.mysite.sbb.domain.user.SiteUser;
import com.mysite.sbb.web.comment.dto.request.CommentRequestDTO;
import com.mysite.sbb.web.comment.dto.request.CommentTargetType;
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
    public Comment addComment(CommentRequestDTO dto, SiteUser author, Long questionId) {
        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setAuthor(author);

        Question question = findQuestionById(questionId);
        comment.setQuestion(question);

        comment.setQuestion(question);

        if (dto.targetType() == CommentTargetType.ANSWER) {
            Answer answer = findAnswerById(dto.targetId());
            comment.setAnswer(answer);
        } else if (dto.targetType() != CommentTargetType.QUESTION) {
            throw new IllegalStateException("잘못된 대상 유형입니다 : " + dto.targetType());
        }

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsForQuestion(int questionId) {
        return commentRepository.findByQuestion_IdAndAnswerIsNull(questionId);
    }

    @Transactional(readOnly = true)
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
