package baekgwa.sbb.model.answer.entity;

import baekgwa.sbb.model.BaseEntity;
import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.user.entity.SiteUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser siteUser;

    @ManyToMany
    private Set<SiteUser> voter;

    @Builder
    private Answer(Integer id, String content, Question question, SiteUser siteUser) {
        this.id = id;
        this.content = content;
        this.question = question;
        this.siteUser = siteUser;
    }

    public static Answer modifyAnswer(Answer oldAnswer, String newContent) {
        return Answer
                .builder()
                .id(oldAnswer.getId())
                .content(newContent)
                .question(oldAnswer.getQuestion())
                .siteUser(oldAnswer.getSiteUser())
                .build();
    }
}
