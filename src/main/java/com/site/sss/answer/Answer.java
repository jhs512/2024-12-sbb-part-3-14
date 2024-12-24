package com.site.sss.answer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.site.sss.Comment.AnswerComment.AnswerComment;
import com.site.sss.question.Question;
import com.site.sss.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private  LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser author;

    @OneToMany(mappedBy = "answer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerComment> commentList;

    @ManyToMany
    Set<SiteUser> voter;
}
