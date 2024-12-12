package baekgwa.sbb.domain.user.service;

import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void create(String username, String email, String password) {
        SiteUser siteUser = SiteUser
                .builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(siteUser);
    }
}
