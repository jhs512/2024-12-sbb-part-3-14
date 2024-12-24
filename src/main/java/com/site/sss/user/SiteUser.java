package com.site.sss.user;

import com.site.sss.Comment.AnswerComment.AnswerComment;
import com.site.sss.Comment.QuestionComment.QuestionComment;
import com.site.sss.answer.Answer;
import com.site.sss.question.Question;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "author",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerComment> AnswerCommentList;

    @OneToMany(mappedBy = "author",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionComment> QuestionCommentList;

    @OneToMany(mappedBy = "author",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answerList;

    @OneToMany(mappedBy = "author",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questionList;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // OAuth 제공자 (Google, Naver 등)
}
