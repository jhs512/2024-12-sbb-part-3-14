package com.site.sss.user;

import com.site.sss.DataNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
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
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteUser not found");
        }
    }

    public boolean checkPasswordMatch(String password1,String password2) {
        return passwordEncoder.matches(password1, password2);
    }

    public void modifyPassword(SiteUser user,String password) {
        String encodePassword=passwordEncoder.encode(password);
        user.setPassword(encodePassword);
        this.userRepository.save(user);
    }

    public void modifyName(SiteUser user, String name) {
        user.setUsername(name);

        this.userRepository.save(user);
    }

    public void deleteUser(String username)
    {
        Optional<SiteUser> _user=userRepository.findByusername(username);
        if (_user.isPresent()) {
            this.userRepository.delete(_user.get());
        } else {
            throw new DataNotFoundException("user not found");
        }



    }

    public SiteUserDTO getUserInfo(SiteUser user) {
        SiteUserDTO siteUserDTO=new SiteUserDTO();
        siteUserDTO.setUsername(user.getUsername());
        siteUserDTO.setEmail(user.getEmail());

        return siteUserDTO;
    }
}
