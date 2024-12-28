package com.programmers.answer;

import com.programmers.data.BaseEntity;
import com.programmers.question.Question;
import com.programmers.recommend.answerRecommend.ARecommend;
import com.programmers.user.SiteUser;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Answer extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private SiteUser siteUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Question question;

    @Setter
    private String content;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "answer")
    private Set<ARecommend> aRecommendSet;
}
