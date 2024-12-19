package org.example.jtsb02.question.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.jtsb02.question.entity.Question;

@Getter
@Builder
public class QuestionDto {

    private Long id;
    private String subject;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int hits;

    public static QuestionDto fromQuestion(Question question) {
        return QuestionDto.builder()
            .id(question.getId())
            .subject(question.getSubject())
            .content(question.getContent())
            .createdAt(question.getCreatedAt())
            .modifiedAt(question.getModifiedAt())
            .hits(question.getHits())
            .build();
    }
}