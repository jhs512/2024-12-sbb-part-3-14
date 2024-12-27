package com.mysite.sbb.web.answer.dto.response;

import com.mysite.sbb.domain.answer.Answer;
import com.mysite.sbb.domain.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnswerResponseDTO {
    private Integer id;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private Integer questionId;
    private String authorName;
    private int voterCount;
    private List<Comment> comments;

    public AnswerResponseDTO(Answer answer, List<Comment> comments) {
        this.id = answer.getId();
        this.content = answer.getContent();
        this.createDate = answer.getCreateDate();
        this.modifyDate = answer.getModifyDate();
        this.questionId = answer.getQuestion().getId();
        this.authorName = answer.getAuthor() != null ? answer.getAuthor().getUsername() : "익명";
        this.voterCount = answer.getVoter().size();
        this.comments = comments;
    }
}
