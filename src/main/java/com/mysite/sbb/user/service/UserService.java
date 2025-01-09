package com.mysite.sbb.user.service;

import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String password, String email) {
        SiteUser siteUser = SiteUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();

        userRepository.save(siteUser);

        return siteUser;
    }

    public Optional<SiteUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
