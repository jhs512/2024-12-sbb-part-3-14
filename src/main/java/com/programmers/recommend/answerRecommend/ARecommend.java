package com.programmers.recommend.answerRecommend;

import com.programmers.answer.Answer;
import com.programmers.user.SiteUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"siteUser_id", "answer_id"})
})
public class ARecommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SiteUser siteUser;

    @ManyToOne
    private Answer answer;
}
