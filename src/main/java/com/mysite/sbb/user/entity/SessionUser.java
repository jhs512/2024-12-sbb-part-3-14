package com.mysite.sbb.user.entity;

import lombok.Getter;

@Getter
public class SessionUser {
    private String username;
    private String email;

    public SessionUser(SiteUser user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
