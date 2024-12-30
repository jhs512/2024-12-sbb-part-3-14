package com.mysite.sbb.domain.question.domain;

import com.mysite.sbb.domain.BaseEntity;
import com.mysite.sbb.domain.answer.doamin.Answer;
import com.mysite.sbb.domain.category.domain.Category;
import com.mysite.sbb.domain.comment.domain.Comment;
import com.mysite.sbb.domain.user.domain.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 200)
    private String subject;

    @Column (columnDefinition =  "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voter;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
