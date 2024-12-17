package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.qustion.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public SiteUser create(String userName, String password, String email) {
        SiteUser user = new SiteUser();
        user.setUsername(userName);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setAdmin(false);
        this.userRepository.save(user);
        return user;
    }

public SiteUser getSiteUser(String username){
    Optional<SiteUser> _siteUser = this.userRepository.findByUsername(username);
    if (_siteUser.isEmpty()) {
        throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
    }
    return _siteUser.get();
}

}
