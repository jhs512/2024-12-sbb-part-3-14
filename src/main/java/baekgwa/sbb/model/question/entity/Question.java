package baekgwa.sbb.model.question.entity;

import baekgwa.sbb.model.BaseEntity;
import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.category.entity.Category;
import baekgwa.sbb.model.user.entity.SiteUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false, exclude = {"answerList"})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder
    private Question(Integer id, String subject, String content, Set<Answer> answerList,
            SiteUser siteUser, Set<SiteUser> voter, Category category) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.answerList = answerList;
        this.siteUser = siteUser;
        this.voter = voter;
        this.category = category;
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