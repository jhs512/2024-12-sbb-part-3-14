package com.ll.pratice1.domain.user.service;

import com.ll.pratice1.DataNotFoundException;
import com.ll.pratice1.domain.user.SiteUser;
import com.ll.pratice1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String password, String email) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        this.userRepository.save(user);
        return user;
    }


    public SiteUser getUser(String username){
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if(siteUser.isPresent()){
            return siteUser.get();
        }else{
            throw new DataNotFoundException("siteUser not found");
        }
    }

    public String generateNumericPassword() {
        // 사용할 문자 집합 정의 (숫자만)
        String digits = "0123456789";

        // SecureRandom 사용
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 패스워드 생성
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(digits.length());
            password.append(digits.charAt(index));
        }
        return password.toString();
    }

    public void updatePassword(SiteUser siteUser, String password){
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
    }

    public SiteUser validatePassword(String username, String password) {
        Optional<SiteUser> siteUser = userRepository.findByusername(username);
        if (!passwordEncoder.matches(password, siteUser.get().getPassword())) {
            return null;
        }
        return siteUser.get();
    }


}
