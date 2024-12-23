package baekgwa.sbb.global.security.siteuser;

import baekgwa.sbb.model.redis.RedisRepository;
import baekgwa.sbb.model.user.entity.SiteUser;
import baekgwa.sbb.model.user.entity.UserRole;
import baekgwa.sbb.model.user.persistence.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RedisRepository redisRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String temporaryPassword = (String) redisRepository.get(username); //임시 비밀번호 유무 확인
        SiteUser siteUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }

        String passwordToUse = (temporaryPassword != null) ? temporaryPassword : siteUser.getPassword(); //임시 비밀번호가 있다면, 임시 비밀번호로 인증하도록
        redisRepository.delete(username); //1회용 비밀번호이므로, 삭제 처리.
        return new User(siteUser.getUsername(), passwordToUse, authorities);
    }
}
