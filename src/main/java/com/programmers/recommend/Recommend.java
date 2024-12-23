package com.programmers.recommend;

import com.programmers.question.Question;
import com.programmers.user.SiteUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"siteUser_id", "question_id"})
})
public class Recommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SiteUser siteUser;

    @ManyToOne
    private Question question;
}
