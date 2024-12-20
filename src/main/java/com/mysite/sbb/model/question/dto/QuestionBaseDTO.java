package com.mysite.sbb.model.question.dto;

import com.mysite.sbb.model.question.entity.Question;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuestionBaseDTO {
    private int id;                         // ID
    private String subject;                 // 제목
    private String content;                 // 내용
    private String authorName;              // 작성자
    private LocalDateTime createDate;       // 생성일
    private LocalDateTime modifyDate;       // 수정일
    private int answerCount;                // 답변 개수

    public QuestionBaseDTO(Question question) {
        this.id = question.getId();
        this.subject = question.getSubject();
        this.content = question.getContent();
        this.authorName = question.getAuthor() != null ? question.getAuthor().getUsername() : "익명";
        this.createDate = question.getCreateDate();
        this.modifyDate = question.getModifyDate();
        this.answerCount = question.getAnswerList().size();
    }
}

