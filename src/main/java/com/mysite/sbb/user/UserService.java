package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public void modifyPassword(SiteUser siteUser, String password) {
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
    }

    public boolean isSamePassword(SiteUser siteUser, String password) {
        return passwordEncoder.matches(password, siteUser.getPassword());
    }

    public SiteUser getUserByEmail(String email) {
        Optional<SiteUser> os = this.userRepository.findByEmail(email);
        if (os.isPresent()) {
            return os.get();
        } else {
            throw new DataNotFoundException("siteuser not found.");
        }
    }

}
