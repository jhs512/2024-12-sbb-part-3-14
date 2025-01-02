package com.mysite.sbb.domain.user.service;

import com.mysite.sbb.domain.user.domain.SiteUser;
import com.mysite.sbb.web.api.v1.user.dto.response.UserResponseDTO;

public interface UserService {

    SiteUser create(String username, String email, String password);

    SiteUser getUser(String username);

    UserResponseDTO getProfile(String username);
}
