package com.programmers.question;

import com.programmers.answer.Answer;
import com.programmers.article.Article;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @LastModifiedDate
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastModifiedAt;

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
}
