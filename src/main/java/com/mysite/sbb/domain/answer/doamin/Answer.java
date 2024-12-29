package com.mysite.sbb.domain.answer;

import com.mysite.sbb.domain.BaseEntity;
import com.mysite.sbb.domain.question.Question;
import com.mysite.sbb.domain.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voter;

    // 추천 수 계산 필드
    @Formula("(SELECT COUNT(AV.VOTER_ID) FROM ANSWER_VOTER AV where AV.answer_id = id)")
    private Integer voterCount;
}
