package com.mysite.sbb.domain.comment;

import com.mysite.sbb.domain.BaseEntity;
import com.mysite.sbb.domain.answer.Answer;
import com.mysite.sbb.domain.question.Question;
import com.mysite.sbb.domain.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private SiteUser author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    public boolean isValidRelation() {
        return (this.question != null && this.answer == null) ||
                (this.question == null && this.answer != null);
    }
}
