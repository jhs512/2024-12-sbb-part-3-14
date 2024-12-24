package com.mysite.sbb.user.service;

import com.mysite.sbb.global.exception.DataNotFoundException;
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

    public SiteUser createUser(String username, String password, String email) {
        SiteUser newUser = new SiteUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);
        this.userRepository.save(newUser);
        return newUser;
    }

    public SiteUser findUser(String name) {
        Optional<SiteUser> siteUserOptional = this.userRepository.findByusername(name);
        if(siteUserOptional.isPresent()){
            return siteUserOptional.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
}
