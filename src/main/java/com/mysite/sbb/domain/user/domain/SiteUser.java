package com.mysite.sbb.domain.user.domain;

import com.mysite.sbb.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private String provider; // OAuth 제공자 (GOOGLE, KAKAO, NAVER 등)

    private String providerId; // OAuth 제공자에서의 고유 ID
}
