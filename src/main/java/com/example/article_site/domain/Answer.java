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
    Set<Author> voter = new HashSet<Author>();  // TODO : Long 값을 보관하게 바꿔서 불필요한 로딩 줄이기 (@ElementCollection)

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    List<Comment> commentList = new ArrayList<>();  // TODO : Comment 가 많아질 것으로 예상된다면, 댓글은 필요할 때 불러오도록 바꾸는 것이 좋다.

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
