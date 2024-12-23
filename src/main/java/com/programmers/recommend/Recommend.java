package com.programmers.recommend;

import com.programmers.question.Question;
import com.programmers.user.SiteUser;
import jakarta.persistence.*;

@Entity
public class Recommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SiteUser siteUser;

    @ManyToOne
    private Question question;
}
