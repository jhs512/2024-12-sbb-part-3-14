package com.kkd.sbb.question;

import com.kkd.sbb.answer.Answer;
import com.kkd.sbb.category.Category;
import com.kkd.sbb.comment.Comment;
import com.kkd.sbb.user.SiteUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    private Set<SiteUser> voter;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @Column(columnDefinition = "integer default 0")
    @NotNull
    private Integer views = 0;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    @ManyToOne
    private Category category;
}
