package com.programmers.question;

import com.programmers.answer.Answer;
import com.programmers.article.Article;
import jakarta.persistence.*;
import java.util.List;
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

    @Transient
    private long answerCount;
}
