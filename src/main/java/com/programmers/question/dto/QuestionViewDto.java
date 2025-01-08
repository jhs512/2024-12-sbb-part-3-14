package com.programmers.question.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record QuestionViewDto (
        String subject,
        String content,
        String authorName,
        long answerCount,
        long commentCount,
        long recommendCount,
        LocalDateTime createdAt,
        LocalDateTime lastUpdatedAt
){
}
