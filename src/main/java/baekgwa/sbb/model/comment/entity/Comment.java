package baekgwa.sbb.model.comment.entity;

import baekgwa.sbb.model.BaseEntity;
import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.user.entity.SiteUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private SiteUser siteUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = true)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = true)
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", nullable = true)
    private Comment parrentComment;

    @Builder
    private Comment(String content, SiteUser siteUser, Question question, Answer answer,
            Comment parrentComment) {
        this.content = content;
        this.siteUser = siteUser;
        this.question = question;
        this.answer = answer;
        this.parrentComment = parrentComment;
    }
}
