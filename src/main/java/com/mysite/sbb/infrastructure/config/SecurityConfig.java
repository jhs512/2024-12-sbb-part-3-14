package com.mysite.sbb.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 요청 흐름
 *
 * [사용자 요청]
 * 1. 사용자가 /user/login 경로로 POST 요청
 *    ↓
 * [Spring Security Filter Chain]
 * 2. UsernamePasswordAuthenticationFilter가 요청 처리
 *    ↓
 * 3. AuthenticationManager가 UserDetailsService를 호출
 *    ↓
 * [UserSecurityService]
 * 4. loadUserByUsername(username)
 *    - 데이터베이스에서 SiteUser 엔티티 조회
 *    - 권한(Role) 리스트 생성
 *    ↓
 * 5. User 객체 반환 (Spring Security 인증 정보 생성)
 *    ↓
 * [Spring Security]
 * 6. 인증 성공: SecurityContextHolder에 사용자 정보 저장
 *    인증 실패: 로그인 페이지로 리다이렉트
 *    ↓
 * 7. 로그인 성공 시 / 경로로 리다이렉트
 */
// Spring Security 설정 클래스
@Configuration
@EnableWebSecurity // Spring Security를 활성화
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize, @PostAuthorize 어노테이션 활성화
public class SecurityConfig {

    /**
     * Spring Security의 필터 체인을 설정합니다.
     * HTTP 요청에 대해 인증 및 권한 설정, 로그인, 로그아웃, CSRF 보호 등을 구성합니다.
     *
     * @param http HttpSecurity 객체 (Spring Security의 주요 설정 도구)
     * @return SecurityFilterChain 객체 (Spring Security의 보안 필터 체인)
     * @throws Exception 예외 처리
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 요청에 대한 권한 설정
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/**")) // 모든 경로에 대해
                        .permitAll() // 접근을 허용함 (인증 불필요)
                )

                // 2. CSRF 보호 설정
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")) // H2 데이터베이스 콘솔 경로에 대해 CSRF 보호를 비활성화
                )

                // 3. X-Frame-Options 헤더 설정
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter( // X-Frame-Options 헤더를 설정
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN // 같은 출처의 iframe만 허용 (H2 콘솔 지원을 위해 필요)
                        ))
                )

                // 4. 로그인 설정
                .formLogin((formLogin) -> formLogin
                        .loginPage("/user/login") // 사용자 정의 로그인 페이지 경로
                        .defaultSuccessUrl("/") // 로그인 성공 시 리다이렉트될 기본 경로
                )

                // 5. 로그아웃 설정
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) // 로그아웃 요청 경로
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 리다이렉트될 경로
                        .invalidateHttpSession(true) // 로그아웃 시 세션 무효화
                );

        return http.build(); // 필터 체인 빌드 및 반환
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder Bean을 생성합니다.
     * BCryptPasswordEncoder를 사용하여 비밀번호를 안전하게 암호화합니다.
     *
     * @return PasswordEncoder 객체
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 암호화 알고리즘을 사용
    }

    /**
     * AuthenticationManager Bean을 생성합니다.
     * Spring Security에서 인증 관리자를 사용하여 인증 처리를 수행합니다.
     *
     * @param authenticationConfiguration 인증 설정 객체
     * @return AuthenticationManager 객체
     * @throws Exception 예외 처리
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // 기본 인증 관리자 반환
    }
}
