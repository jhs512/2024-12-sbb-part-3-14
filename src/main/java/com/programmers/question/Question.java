package com.programmers.question;

import com.programmers.answer.Answer;
import com.programmers.data.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Question extends BaseEntity {
    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "question")
    private List<Answer> answerList;

    @Transient
    private long answerCount;
}
