package com.ll.pratice1.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String username;

    String password;

    //@Column(unique = true) 소셜 로그인 상황에서
    String email;

    private String providerTypeCode;
}
