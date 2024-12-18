package com.mysite.sbb.dto;

import com.mysite.sbb.domain.Question;
import com.mysite.sbb.domain.SiteUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuestionListDTO {
    private int id;
    private int answerCount;         // 답변 개수만 가져옴
    private String subject;
    private String content;
    private SiteUser author;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public QuestionListDTO(Question question) {
        this.id = question.getId();
        this.subject = question.getSubject();
        this.content = question.getContent();
        this.answerCount = question.getAnswerList().size();
        this.author = question.getAuthor();
        this.createDate = question.getCreateDate();
        this.modifyDate = question.getModifyDate();
    }
}
