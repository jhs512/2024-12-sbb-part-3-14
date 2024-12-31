package com.mysite.sbb.global.config;

import com.mysite.sbb.domain.user.service.CustomOAuth2UserService;
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

// Spring Security 설정 클래스
@Configuration
@EnableWebSecurity // Spring Security를 활성화
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize, @PostAuthorize 어노테이션 활성화
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 권한 설정
        configureAuthorization(http);

        // OAuth2 로그인 설정
        configureOAuth2Login(http);

        // CSRF 보호 설정
        configureCsrf(http);

        // 헤더 설정
        configureHeaders(http);

        // 로그인 설정
        configureLogin(http);

        // 로그아웃 설정
        configureLogout(http);

        return http.build(); // 필터 체인 빌드 및 반환
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/**"))
                .permitAll() // 모든 요청 허용 (인증 불필요)
        );
    }

    private void configureOAuth2Login(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/user/login") // 로그인 페이지
                .defaultSuccessUrl("/") // 로그인 성공 시 리디렉션
                .failureUrl("/user/login?error=true") // 로그인 실패 시 리디렉션
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService))          // 사용자 정보 처리
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/login/oauth2/code/*")) // 리다이렉트 URI 기본 경로 설정
        );
    }

    private void configureCsrf(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(
                        new AntPathRequestMatcher("/h2-console/**"),
                        new AntPathRequestMatcher("/api/v1/**")
                )
        );
    }

    private void configureHeaders(HttpSecurity http) throws Exception {
        http.headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN // H2 콘솔 지원을 위해 SAMEORIGIN 설정
                ))
        );
    }

    private void configureLogin(HttpSecurity http) throws Exception {
        http.formLogin(formLogin -> formLogin
                .loginPage("/user/login") // 사용자 정의 로그인 페이지
                .defaultSuccessUrl("/") // 로그인 성공 시 리다이렉트 경로
        );
    }

    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) // 로그아웃 요청 경로
                .logoutSuccessUrl("/") // 로그아웃 성공 시 리다이렉트 경로
                .invalidateHttpSession(true) // 세션 무효화
        );
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 암호화 알고리즘을 사용
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // 기본 인증 관리자 반환
    }
}
