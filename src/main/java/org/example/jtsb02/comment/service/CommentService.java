package org.example.jtsb02.comment.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.comment.entity.Comment;
import org.example.jtsb02.comment.form.CommentForm;
import org.example.jtsb02.comment.repository.CommentRepository;
import org.example.jtsb02.common.exception.DataNotFoundException;
import org.example.jtsb02.member.dto.MemberDto;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.repository.MemberRepository;
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
}
