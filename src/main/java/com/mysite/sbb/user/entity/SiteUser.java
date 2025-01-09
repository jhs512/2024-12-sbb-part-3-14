package com.mysite.sbb.user.entity;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.comment.entity.Comment;
import com.mysite.sbb.question.entity.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 50, unique = true)
    private String username;

    @Column(columnDefinition = "TEXT")
    private String password;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    List<Comment> comments = new ArrayList<>();
}
