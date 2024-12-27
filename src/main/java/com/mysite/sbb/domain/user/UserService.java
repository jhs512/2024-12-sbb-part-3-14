package com.mysite.sbb.domain.user;

public interface UserService {

    SiteUser create(String username, String email, String password);

    SiteUser getUser(String username);
}
