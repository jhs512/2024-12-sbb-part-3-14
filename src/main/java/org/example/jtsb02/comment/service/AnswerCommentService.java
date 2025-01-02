package org.example.jtsb02.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.answer.repository.AnswerRepository;
import org.example.jtsb02.comment.dto.CommentDto;
import org.example.jtsb02.comment.entity.Comment;
import org.example.jtsb02.comment.form.CommentForm;
import org.example.jtsb02.comment.repository.CommentRepository;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerCommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;

    public void createAnswerComment(Long answerId, CommentForm commentForm, MemberDto memberDto) {
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new DataNotFoundException("answer not found"));
        commentRepository.save(
            Comment.of(commentForm.getContent(), answer, Member.fromMemberDto(memberDto)));
    }

    public CommentDto getAnswerComment(Long commentId) {
        return CommentDto.AnswerCommentDtoFromComment(commentRepository.findById(commentId)
            .orElseThrow(() -> new DataNotFoundException("comment not found")));
    }
}
