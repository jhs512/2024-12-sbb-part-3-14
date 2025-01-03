package com.kkd.sbb.user;

import com.kkd.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email,String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser create(String registrationId, String userName,
                           String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(userName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRegisterId(registrationId);
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if(siteUser.isPresent()) {
            return siteUser.get();
        }else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public SiteUser update(SiteUser user, String newPassword) {
        user.setPassword(this.passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
        return user;
    }

    public boolean isMatch(String rawPassword, String encodedPassword) {
        return this.passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public SiteUser getUserByEmail(String email) {
        Optional<SiteUser> siteUser = this.userRepository.findByEmail(email);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("Email not found!!");
        }
    }

    public SiteUser socialLogin(String registrationId, String username, String email) {
        Optional<SiteUser> user = this.userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            return this.create(registrationId, username, email, "");
        }
    }
}
