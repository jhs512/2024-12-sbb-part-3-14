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
import com.mysite.sbb.web.api.v1.comment.dto.response.CommentListResponseDTO;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Transactional(readOnly = true)
    @Override
    public Page<CommentListResponseDTO> getList(int page, String kw) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        Specification<Comment> spec = search(kw);
        return commentRepository.findAll(spec, pageable)
                .map(CommentListResponseDTO::new);
    }

    private Specification<Comment> search(String kw) {
        return (Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);

            // Join 처리
            Join<Comment, Question> questionJoin = root.join("question", JoinType.LEFT);
            Join<Comment, SiteUser> authorJoin = root.join("author", JoinType.LEFT);

            // 검색 조건
            return cb.or(
                    cb.like(root.get("content"), "%" + kw + "%"),    // 댓글 내용 검색
                    cb.like(questionJoin.get("subject"), "%" + kw + "%"),  // 질문 제목 검색
                    cb.like(authorJoin.get("username"), "%" + kw + "%")    // 작성자 검색
            );
        };
    }
}
