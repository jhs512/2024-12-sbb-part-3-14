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
public class Answer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToMany
    Set<Author> voter = new HashSet<Author>();

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    List<Comment> commentList = new ArrayList<>();

    protected Answer() {}

    public static Answer createAnswer(Question question, String content, Author author) {
        Answer answer = new Answer();
        answer.question = question;
        answer.content = content;
        answer.author = author;
        answer.createDate = LocalDateTime.now();
        question.getAnswerList().add(answer);
        return answer;
    }

    public static void modifyAnswer(Answer answer, String content) {
        answer.modifyDate = LocalDateTime.now();
        answer.content = content;
    }
}
