package baekgwa.sbb.model.question.entity;

import baekgwa.sbb.model.answer.entity.Answer;
import baekgwa.sbb.model.user.entity.SiteUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser siteUser;

    @Builder
    private Question(String subject, String content, LocalDateTime createDate,
            List<Answer> answerList,
            SiteUser siteUser) {
        this.subject = subject;
        this.content = content;
        this.createDate = createDate;
        this.answerList = answerList;
        this.siteUser = siteUser;
    }
}