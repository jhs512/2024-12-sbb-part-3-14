package com.example.article_site.dto.profile;

import com.example.article_site.domain.Answer;
import com.example.article_site.dto.AnswerListDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnswerProfileDto {
    private String content;
    private LocalDateTime createDate;

    public static AnswerProfileDto createAnswerProfileDto(Answer answer){
        AnswerProfileDto dto = new AnswerProfileDto();
        dto.setContent(answer.getContent());
        dto.setCreateDate(answer.getCreateDate());
        return dto;
    }
}
