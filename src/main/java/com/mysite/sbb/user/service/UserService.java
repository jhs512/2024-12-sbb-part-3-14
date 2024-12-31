package com.mysite.sbb.user.service;

import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.exception.DataNotFoundException;
import com.mysite.sbb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email,String password) {
        SiteUser siteUser = new SiteUser();
        siteUser.setUsername(username);
        siteUser.setEmail(email);
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
        return siteUser;
    }

    public SiteUser getUser(String username) {
        System.out.println("FindByUsername 호출 전: " + username);
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        System.out.println("FindByUsername 결과: " + siteUser);

        if(siteUser.isPresent()) {
            System.out.println("현재 사용자 이름: " + username);
            return siteUser.get();
        } else {
            System.out.println("사용자를 찾을 수 없습니다: " + username);
            throw new DataNotFoundException("User not found");
        }
    }

    public boolean isUsingTemporaryPassword(String username) {
        SiteUser user = userRepository.findByUsername(username)
                .orElse(null);

        return user.isTempPassword();
    }

    public SiteUser getUserByUsername(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);

        if(siteUser.isPresent()) {
            System.out.println("현재 사용자 이름: " + username);
            return siteUser.get();
        } else {
            System.out.println("사용자를 찾을 수 없습니다: " + username);
            throw new DataNotFoundException("User not found");
        }
    }
}
