package com.example.article_site.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
public class Author {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private long id;

    @Column(unique = true)
    private String username;

    private String email;

    private String password;

    private String picture;    // 프로필 이미지 URL

    private String provider;   // 소셜 로그인 제공자

    private String providerId; // 소셜 로그인 제공자에서의 ID

    protected Author() {}

    public static Author createAuthor(String username, String email, String password) {
        Author author = new Author();
        author.username = username;
        author.email = email;
        author.password = password;
        return author;
    }

    public void modifyPassword(String newPassword) {
        this.password = newPassword;
    }

    // 소셜 로그인용 정적 팩토리 메서드
    public static Author createSocialAuthor(String username, String email, String picture, String provider, String providerId) {
        Author author = new Author();
        author.username = username;
        author.email = email;
        author.password = UUID.randomUUID().toString(); // 소셜 로그인 유저는 임의의 비밀번호
        author.picture = picture;
        author.provider = provider;
        author.providerId = providerId;
        return author;
    }

    // 소셜 로그인 정보 업데이트 메서드
    public Author update(String username, String picture) {
        this.username = username;
        this.picture = picture;
        return this;
    }

    // 소셜 계정인지 확인하는 메서드
    public boolean isSocialAccount() {
        return provider != null && !provider.isEmpty();
    }

}
