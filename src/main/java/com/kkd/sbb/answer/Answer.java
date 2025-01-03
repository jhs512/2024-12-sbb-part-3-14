package com.kkd.sbb.answer;

import com.kkd.sbb.comment.Comment;
import com.kkd.sbb.question.Question;
import com.kkd.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    private Set<SiteUser> voter;

    @ManyToOne
    private Question question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;
}
