package com.site.sss.Comment.QuestionComment;

import com.site.sss.Comment.Comment;
import com.site.sss.question.Question;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class QuestionComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}
