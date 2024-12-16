package baekgwa.sbb.model.question.entity;

import baekgwa.sbb.model.BaseEntity;
import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.user.entity.SiteUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private Set<Answer> answerList;

    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser siteUser;

    @ManyToMany
    private Set<SiteUser> voter;

    @Builder
    public Question(Integer id, String subject, String content, Set<Answer> answerList,
            SiteUser siteUser) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.answerList = answerList;
        this.siteUser = siteUser;
    }

    public static Question modifyQuestion(Question oldQuestion, String subject, String content) {
        return Question
                .builder()
                .id(oldQuestion.getId())
                .subject(subject)
                .content(content)
                .answerList(oldQuestion.getAnswerList())
                .siteUser(oldQuestion.getSiteUser())
                .build();
    }
}