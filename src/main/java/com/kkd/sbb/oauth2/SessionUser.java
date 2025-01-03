package com.kkd.sbb.oauth2;


import com.kkd.sbb.user.SiteUser;
import lombok.Getter;

@Getter
public class SessionUser {

    private String username;
    private String email;
    private String password;

    public SessionUser(SiteUser user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
}
