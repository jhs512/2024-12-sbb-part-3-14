package com.example.article_site.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    Answer answer;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @CreatedDate
    private LocalDateTime createDate;

    public static Comment createComment(Answer answer, Author author, String content) {
        Comment comment = new Comment();
        comment.answer = answer;
        comment.author = author;
        comment.content = content;
        comment.createDate = LocalDateTime.now();
        answer.getCommentList().add(comment);
        return comment;
    }
}
