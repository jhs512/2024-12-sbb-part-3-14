package com.mysite.sbb.web.api.v1.answer.dto.response;

import com.mysite.sbb.domain.answer.doamin.Answer;

import java.time.LocalDateTime;

public record AnswerListResponseDTO(
        Integer id,
        String content,
        Integer questionId,
        String authorName,
        Integer voterCount,
        LocalDateTime createDate
) {

    public AnswerListResponseDTO(Answer answer) {
        this(
                answer.getId(),
                answer.getContent(),
                answer.getQuestion().getId(),
                answer.getAuthor().getUsername(),
                answer.getVoterCount(),
                answer.getCreateDate()
        );
    }
}