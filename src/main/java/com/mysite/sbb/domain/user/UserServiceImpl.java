package com.mysite.sbb.domain.user;


import com.mysite.sbb.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository; // 사용자 정보를 저장 및 조회하는 레포지토리
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 PasswordEncoder

    @Override
    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username); // 사용자 이름 설정
        user.setEmail(email); // 이메일 설정
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화 후 설정
        userRepository.save(user); // 데이터베이스에 사용자 저장
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public SiteUser getUser(String username) {
        return userRepository.findByusername(username)
                .orElseThrow(() -> new DataNotFoundException("not found user :" + username));
    }
}
