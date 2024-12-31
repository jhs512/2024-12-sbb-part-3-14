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

@Configuration
@EnableWebSecurity // 웹 보안 기능 활성화
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize, @PostAuthorize 어노테이션을 활성화
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

        // HTTP 헤더 설정
        configureHeaders(http);

        // 폼 기반 로그인 설정
        configureLogin(http);

        // 로그아웃 설정
        configureLogout(http);

        // 빌드하고 반환
        return http.build();
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/**"))
                        .permitAll() // 모든 요청을 허용
        );
    }


    private void configureOAuth2Login(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/user/login") // 사용자 정의 로그인 페이지 URL
                .defaultSuccessUrl("/") // 로그인 성공 시 리디렉션할 기본 URL
                .failureUrl("/user/login?error=true") // 로그인 실패 시 리디렉션할 URL
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService)) // 사용자 정보 처리에 사용할 커스텀 OAuth2UserService 설정
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/login/oauth2/code/*")) // OAuth2 리다이렉트 URI 기본 경로 설정
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
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                        ))
        );
    }

    private void configureLogin(HttpSecurity http) throws Exception {
        http.formLogin(formLogin -> formLogin
                .loginPage("/user/login") // 사용자 정의 로그인 페이지 URL
                .defaultSuccessUrl("/") // 로그인 성공 시 리디렉션할 기본 URL
                .permitAll() // 로그인 페이지는 모든 사용자에게 접근을 허용
        );
    }

    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) // 로그아웃 요청 URL
                .logoutSuccessUrl("/") // 로그아웃 성공 시 리디렉션할 URL
                .invalidateHttpSession(true) // 로그아웃 시 세션을 무효화
                .deleteCookies("JSESSIONID") // 로그아웃 시 쿠키를 삭제하여 세션을 완전히 종료
        );
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 해시 함수를 사용하여 비밀번호를 암호화합니다.
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
