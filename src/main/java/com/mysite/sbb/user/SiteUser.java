package com.mysite.sbb.user;

import com.mysite.sbb.comment.Comment;
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

    @Column(unique = true) // 중복 불가
    private String username;

    private String password;

    @Column(unique = true) // 중복 불가
    private String email;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    List<Comment> commentList;
}
