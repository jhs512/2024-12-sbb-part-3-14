package com.mysite.sbb.web.api.common.v1.question.dto.response;

import com.mysite.sbb.domain.question.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class QuestionListResponseDTO{
    private long id;                         // ID
    private String subject;                 // 제목
    private String content;                 // 내용
    private String authorName;              // 작성자
    private LocalDateTime createDate;       // 생성일
    private LocalDateTime modifyDate;       // 수정일
    private int answerCount;                // 답변 개수

    public QuestionListResponseDTO(Question question) {
        this.id = question.getId();
        this.subject = question.getSubject();
        this.content = question.getContent();
        this.authorName = question.getAuthor() != null ? question.getAuthor().getUsername() : "익명";
        this.createDate = question.getCreateDate();
        this.modifyDate = question.getModifyDate();
        this.answerCount = question.getAnswerList().size();
    }
}
