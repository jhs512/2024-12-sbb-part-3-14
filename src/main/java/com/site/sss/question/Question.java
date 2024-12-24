package com.site.sss.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.site.sss.Category.Category;
import com.site.sss.Comment.QuestionComment.QuestionComment;
import com.site.sss.answer.Answer;
import jakarta.persistence.*;
import jakarta.persistence.ManyToOne;
import com.site.sss.user.SiteUser;

import lombok.Getter;
import lombok.Setter;


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

    @OneToMany(mappedBy = "question" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answerList;

    @OneToMany(mappedBy = "question" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionComment> CommentList;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voter;

    private int views;
}
