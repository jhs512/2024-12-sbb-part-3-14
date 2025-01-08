package com.ll.pratice1.domain.comment;

import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.question.Question;
import com.ll.pratice1.domain.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createDate;

    @ManyToOne
    private Answer answer;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser siteUser;
}
