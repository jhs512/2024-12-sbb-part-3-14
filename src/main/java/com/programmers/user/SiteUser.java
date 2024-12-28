package com.programmers.user;

import com.programmers.data.BaseEntity;
import com.programmers.recommend.answerRecommend.ARecommend;
import com.programmers.recommend.questionRecommend.QRecommend;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SiteUser extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String username;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;
}
