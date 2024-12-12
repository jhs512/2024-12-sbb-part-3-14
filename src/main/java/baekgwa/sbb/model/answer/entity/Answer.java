package baekgwa.sbb.model.answer.entity;

import baekgwa.sbb.model.question.entity.Question;
import baekgwa.sbb.model.user.entity.SiteUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser siteUser;

    @Builder
    private Answer(String content, LocalDateTime createDate, Question question, SiteUser siteUser) {
        this.content = content;
        this.createDate = createDate;
        this.question = question;
        this.siteUser = siteUser;
    }
}
