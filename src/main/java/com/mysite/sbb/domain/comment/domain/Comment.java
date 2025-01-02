package com.mysite.sbb.domain.comment.domain;

import com.mysite.sbb.domain.BaseEntity;
import com.mysite.sbb.domain.answer.domain.Answer;
import com.mysite.sbb.domain.question.domain.Question;
import com.mysite.sbb.domain.user.domain.SiteUser;
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

    public int getQuestionId() {
        if (this.question != null) {
            return question.getId();
        }

        throw new IllegalStateException("유효한 질문 ID를 찾을 수 없습니다.");
    }

}
