package com.mysite.sbb.Comment;

import com.mysite.sbb.Answer.Answer;
import com.mysite.sbb.Question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Question question;  // 질문에 달린 댓글

    @ManyToOne
    private Answer answer;  // 답변에 달린 댓글

    @ManyToOne
    private SiteUser author;  // 댓글 작성자

    @Column(nullable = false)
    private String content;  // 댓글 내용

    private LocalDateTime createDate;  // 댓글 작성일시
}