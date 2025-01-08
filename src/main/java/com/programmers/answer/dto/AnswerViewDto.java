package com.programmers.answer.dto;

import java.time.LocalDateTime;

public record AnswerViewDto (
        String content,
        String authorName,
        long commentCount,
        long recommendCount,
        LocalDateTime createdAt,
        LocalDateTime lastUpdatedAt
){
}
