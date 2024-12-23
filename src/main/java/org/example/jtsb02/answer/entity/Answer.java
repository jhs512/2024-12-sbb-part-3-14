package org.example.jtsb02.answer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jtsb02.answer.dto.AnswerDto;
import org.example.jtsb02.question.entity.Question;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    public static Answer of(String content, Question question) {
        return Answer.builder()
            .content(content)
            .createdAt(LocalDateTime.now())
            .question(question)
            .build();
    }

    public static Answer fromAnswerDto(AnswerDto answerDto) {
        return Answer.builder()
            .id(answerDto.getId())
            .content(answerDto.getContent())
            .createdAt(answerDto.getCreatedAt())
            .modifiedAt(answerDto.getModifiedAt())
            .question(Question.OnlyIdFromQuestionDto(answerDto.getQuestion()))
            .build();
    }
}
