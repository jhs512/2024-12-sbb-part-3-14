package com.site.sss.Comment.AnswerComment;

import com.site.sss.Comment.Comment;
import com.site.sss.answer.Answer;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AnswerComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    private Answer answer;
}
