package com.example.article_site.dto.profile;

import com.example.article_site.domain.Question;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuestionProfileDto {
    private String subject;
    private LocalDateTime createDate;

    public static QuestionProfileDto createQuestionProfileDto(Question question) {
        QuestionProfileDto dto = new QuestionProfileDto();
        dto.setSubject(question.getSubject());
        dto.setCreateDate(question.getCreateDate());
        return dto;
    }
}
