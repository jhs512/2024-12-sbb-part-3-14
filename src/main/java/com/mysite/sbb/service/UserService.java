package com.mysite.sbb.service;

import com.mysite.sbb.domain.SiteUser;

public interface UserService {
    SiteUser create(String username, String email, String password);

    SiteUser getUser(String username);
}
