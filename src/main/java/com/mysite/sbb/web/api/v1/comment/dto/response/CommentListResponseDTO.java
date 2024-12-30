package com.mysite.sbb.web.api.v1.comment.dto.response;

import com.mysite.sbb.domain.comment.domain.Comment;

import java.time.LocalDateTime;

public record CommentListResponseDTO(
        Long id,
        String content,
        Integer questionId,
        String authorName,
        LocalDateTime createDate
) {

    public CommentListResponseDTO(Comment comment) {
        this(
                comment.getId(),
                comment.getContent(),
                comment.getQuestion() != null ? comment.getQuestion().getId() : null,
                comment.getAuthor().getUsername(),
                comment.getCreateDate()
        );
    }
}