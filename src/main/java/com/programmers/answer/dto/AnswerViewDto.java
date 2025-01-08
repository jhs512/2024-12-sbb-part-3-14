package com.programmers.answer.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnswerViewDto (
        long answerId,
        String content,
        String authorName,
        long commentCount,
        long recommendCount,
        LocalDateTime createdAt,
        LocalDateTime lastUpdatedAt
){
}
