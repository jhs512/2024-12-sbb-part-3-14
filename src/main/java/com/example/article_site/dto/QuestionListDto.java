package com.example.article_site.dto;

import com.example.article_site.domain.Question;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * question_list.html 에서 사용할 데이터 정보를 취합하여 만든 Dto
 */
@Getter
@Setter
public class QuestionListDto {
    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createDate;
    private String author;
    private Integer answerCount;
    private Long views;

    public static QuestionListDto createQuestionListDto(Question question) {
        QuestionListDto dto = new QuestionListDto();
        dto.setId(question.getId());
        dto.setSubject(question.getSubject());
        dto.setContent(question.getContent());
        dto.setCreateDate(question.getCreateDate());
        dto.setAuthor(question.getAuthor().getUsername());
        dto.setAnswerCount(question.getAnswerList().size());
        dto.setViews(question.getViews());
        return dto;
    }
}
