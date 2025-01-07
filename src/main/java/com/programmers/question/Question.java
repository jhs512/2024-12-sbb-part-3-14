package com.programmers.question;

import com.programmers.answer.Answer;
import com.programmers.article.Article;
import com.programmers.data.BaseEntity;
import com.programmers.recommend.questionRecommend.QRecommend;
import com.programmers.user.SiteUser;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Question{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false, updatable = false)
    private Article article;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "question")
    private List<Answer> answerList;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "question")
    private Set<QRecommend> qRecommendSet;

    @Transient
    private long recommendationCount;

    @Transient
    private long answerCount;
}
