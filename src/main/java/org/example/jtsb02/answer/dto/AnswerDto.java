package org.example.jtsb02.answer.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.example.jtsb02.answer.entity.Answer;
import org.example.jtsb02.question.dto.QuestionDto;

@Getter
@Builder
public class AnswerDto {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private QuestionDto question;

    public static AnswerDto fromAnswer(Answer answer) {
        return AnswerDto.builder()
            .id(answer.getId())
            .content(answer.getContent())
            .createdAt(answer.getCreatedAt())
            .modifiedAt(answer.getModifiedAt())
            .question(QuestionDto.OnlyIdFromQuestion(answer.getQuestion()))
            .build();
    }
}
