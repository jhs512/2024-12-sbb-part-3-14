package org.example.jtsb02.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.jtsb02.comment.dto.CommentDto;
import org.example.jtsb02.comment.entity.Comment;
import org.example.jtsb02.comment.form.CommentForm;
import org.example.jtsb02.comment.repository.CommentRepository;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.question.entity.Question;
import org.example.jtsb02.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionCommentService {

    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final CommentService commentService;

    public void createQuestionComment(Long questionId, CommentForm commentForm, MemberDto memberDto) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new DataNotFoundException("question not found"));
        commentRepository.save(
            Comment.of(commentForm.getContent(), question, Member.fromMemberDto(memberDto)));
    }

    public CommentDto getQuestionComment(Long commentId) {
        return CommentDto.QuestionCommentDtoFromComment(commentRepository.findById(commentId)
            .orElseThrow(() -> new DataNotFoundException("comment not found")));
    }

    public void modifyQuestionComment(Long commentId, CommentForm CommentForm) {
        commentService.modifyComment(commentId, CommentForm);
    }
}
