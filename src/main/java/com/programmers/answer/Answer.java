package com.programmers.answer;

import com.programmers.data.BaseEntity;
import com.programmers.question.Question;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Answer extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Question question;

    @Setter
    private String content;
}
