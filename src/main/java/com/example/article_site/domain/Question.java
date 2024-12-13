package com.example.article_site.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
public class Question {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private long id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    private List<Answer> answerList = new ArrayList<Answer>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    Set<Author> voter = new HashSet<Author>();

    private Long views;

    protected Question() {}

    public void incemenetViews(){
        views++;
    }

    public static Question createQuestion(String subject, String content, Category category, Author author) {
        Question question = new Question();
        question.subject = subject;
        question.content = content;
        question.author = author;
        question.createDate = LocalDateTime.now();
        question.modifyDate = null;
        question.views = 0L;
        question.category = category;
        return question;
    }

    public static void modifyQuestion(Question question, String subject, String content, Category category) {
        question.subject = subject;
        question.content = content;
        question.modifyDate = LocalDateTime.now();
        question.category = category;
    }
}
