package com.mysite.sbb.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Spring Security에서 사용자 인증 정보를 처리하는 서비스 클래스.
 * UserDetailsService를 구현하여 사용자 이름(username) 기반으로 인증 데이터를 제공.
 */
@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository; // 사용자 정보를 데이터베이스에서 조회하기 위한 레포지토리

    /**
     * 사용자 이름(username)으로 사용자 정보를 조회하고 인증 객체(UserDetails)를 반환.
     *
     * @param username 사용자 이름
     * @return UserDetails Spring Security 인증 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        System.out.println("사용자 인증 로직 호출됨: " + username); // 호출 로그 출력
        Optional<SiteUser> _siteUser = this.userRepository.findByusername(username);
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다."); // 사용자 없음 예외
        }

        SiteUser siteUser = _siteUser.get(); // 사용자 정보 가져오기
        List<GrantedAuthority> authorities = new ArrayList<>(); // 사용자 권한 리스트

        // 2. 권한 부여
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue())); // ROLE_ADMIN
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue())); // ROLE_USER
        }

        // 3. Spring Security의 User 객체 반환 (username, password, 권한)
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
    }
}
