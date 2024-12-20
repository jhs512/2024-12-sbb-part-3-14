package com.mysite.sbb.model.comment.entity;

import com.mysite.sbb.model.answer.entity.Answer;
import com.mysite.sbb.model.question.entity.Question;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String author;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    @ManyToOne(optional = true)
    private Question question;

    @ManyToOne(optional = true)
    private Answer answer;

    @ManyToOne(optional = true)
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();
}
