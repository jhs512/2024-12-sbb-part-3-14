package com.mysite.sbb.domain.question;

import com.mysite.sbb.domain.BaseEntity;
import com.mysite.sbb.domain.answer.Answer;
import com.mysite.sbb.domain.category.Category;
import com.mysite.sbb.domain.user.SiteUser;
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

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voter;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
