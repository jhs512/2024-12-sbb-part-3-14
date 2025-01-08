package com.ll.pratice1.domain.question;

import com.ll.pratice1.domain.answer.Answer;
import com.ll.pratice1.domain.category.Category;
import com.ll.pratice1.domain.comment.Comment;
import com.ll.pratice1.domain.user.SiteUser;
import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    private Set<SiteUser> voter;

    @ManyToOne
    private Category category;

    private LocalDateTime recentAnswerDate;
    private LocalDateTime recentCommentDate;

    @Column(columnDefinition = "INT DEFAULT 0", nullable = false)
    private int viewCount;

}
