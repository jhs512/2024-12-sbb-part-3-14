package org.example.jtsb02.comment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.comment.dto.CommentDto;
import org.example.jtsb02.comment.entity.Comment;
import org.example.jtsb02.comment.form.CommentForm;
import org.example.jtsb02.comment.repository.CommentRepository;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public void modifyComment(Long commentId, CommentForm commentForm) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new DataNotFoundException("comment not found"));
        commentRepository.save(comment.toBuilder()
            .content(commentForm.getContent())
            .modifiedAt(LocalDateTime.now())
            .build());
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public void voteComment(Long commentId, MemberDto memberDto) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new DataNotFoundException("comment not found"));
        Member member = memberRepository.findById(memberDto.getId())
            .orElseThrow(() -> new DataNotFoundException("member not found"));

        if (comment.getVoter().contains(member)) {
            comment.getVoter().remove(member);
        } else {
            comment.getVoter().add(member);
        }
        commentRepository.save(comment);
    }

    public Page<CommentDto> getCommentsByAuthorId(Long authorId, int page) {
        List<Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        return commentRepository.findByAuthorId(authorId, pageable).map(this::convertToDto);
    }

    private CommentDto convertToDto(Comment comment) {
        if (comment.getQuestion() != null) {
            return CommentDto.QuestionCommentDtoFromComment(comment);
        } else if (comment.getAnswer() != null) {
            return CommentDto.AnswerCommentDtoFromComment(comment);
        } else {
            throw new IllegalArgumentException("유효하지 않은 댓글 입니다.");
        }
    }

    public Page<CommentDto> getComments(int page) {
        List<Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        return commentRepository.findAll(pageable).map(this::convertToDto);
    }
}
