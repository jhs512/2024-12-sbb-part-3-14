package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.qustion.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public SiteUser create(String userName, String password, String email) {
        Optional<SiteUser> _siteUser = this.userRepository.findByUsername(userName);
        if (_siteUser.isPresent()) {
            return null;
        }
        SiteUser user = new SiteUser();
        user.setUsername(userName);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setUserRole(UserRole.USER);
        this.userRepository.save(user);
        return user;
    }
    public void changePassword(String password,SiteUser user){
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
    }

    public void changePasswordByEmail(String password,String email){
        SiteUser user = getSiteUserEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
    }

    public boolean checkPassword(String password, SiteUser user) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    public SiteUser getSiteUser(String username) {
        Optional<SiteUser> _siteUser = this.userRepository.findByUsername(username);
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        return _siteUser.get();
    }

    public SiteUser whenSocialLogin(String providerTypeCode, String username){
        SiteUser user = getSiteUser(username);
        if (user == null) {
            user = new SiteUser();
            user.setUsername(username);
            user.setProviderTypeCode(providerTypeCode);
            user.setUserRole(UserRole.USER);
            this.userRepository.save(user);
        }
        return user;
    }
    public SiteUser getSiteUserEmail(String email){
        Optional<SiteUser> _siteUser = this.userRepository.findByUsername(email);
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        return _siteUser.get();
    }
}
