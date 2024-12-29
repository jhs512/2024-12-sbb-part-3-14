package com.mysite.sbb.domain.user;

import com.mysite.sbb.web.api.v1.user.dto.response.UserResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    SiteUser create(String username, String email, String password);

    SiteUser getUser(String username);

    @Transactional(readOnly = true)
    UserResponseDTO getProfile(String username);
}
