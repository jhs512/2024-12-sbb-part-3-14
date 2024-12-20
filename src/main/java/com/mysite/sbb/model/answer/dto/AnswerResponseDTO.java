package com.mysite.sbb.model.answer.dto;

import com.mysite.sbb.model.answer.entity.Answer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnswerResponseDTO {
    private Integer id;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Integer questionId;
    private String authorName;
    private int voterCount;

    public AnswerResponseDTO(Answer answer) {
        this.id = answer.getId();
        this.content = answer.getContent();
        this.createDate = answer.getCreateDate();
        this.modifyDate = answer.getModifyDate();
        this.questionId = answer.getQuestion().getId();
        this.authorName = answer.getAuthor() != null ? answer.getAuthor().getUsername() : "익명";
        this.voterCount = answer.getVoter().size();
    }
}
