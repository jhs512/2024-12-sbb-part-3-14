package com.mysite.sbb.user.service;

import com.mysite.sbb.answer.repository.AnswerRepository;
import com.mysite.sbb.comment.repository.CommentRepository;
import com.mysite.sbb.entity.SiteUser;
import com.mysite.sbb.exception.DataNotFoundException;
import com.mysite.sbb.question.repository.QuestionRepository;
import com.mysite.sbb.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    public SiteUser create(String username, String email,String password) {
        SiteUser siteUser = new SiteUser();
        siteUser.setUsername(username);
        siteUser.setEmail(email);
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
        return siteUser;
    }

    public String getCurrentUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

/*        if(principal instanceof User) {
            return ((User) principal).getUsername();
        } else {
            return principal.toString();
        }*/

        System.out.println("Principal class: " + principal.getClass().getName());

        if (principal instanceof SiteUser) {
            return ((SiteUser) principal).getUsername();  // 일반 로그인 사용자
        } else if (principal instanceof OAuth2User) {
            String nickname = ((OAuth2User) principal).getAttribute("nickname");
            System.out.println("nickname : " + nickname);

            return (nickname != null) ? nickname : ((OAuth2User) principal).getAttribute("email");
        } else if(principal instanceof String){
            return (String) principal;
        } else {
            throw new IllegalStateException("알 수 없는 사용자 유형입니다.");
        }
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);

        if(siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("User not found");
        }
    }

    public boolean isUsingTemporaryPassword(String username) {
        SiteUser user = userRepository.findByUsername(username)
                .orElse(null);

        return user.isTempPassword();
    }

    public SiteUser getUserByUsername(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);

        if(siteUser.isPresent()) {
            System.out.println("현재 사용자 이름: " + username);
            return siteUser.get();
        } else {
            System.out.println("사용자를 찾을 수 없습니다: " + username);
            throw new DataNotFoundException("User not found");
        }
    }

    public Map<String,Object> getUserProfile(String username, Pageable pageable) {
        SiteUser user = this.getUser(username);

        Map<String,Object> profileData = new HashMap<>();
        profileData.put("user", user);
        profileData.put("questions",questionRepository.findByAuthor(user,pageable));
        profileData.put("answers",answerRepository.findByAuthor(user,pageable));
        profileData.put("comments",commentRepository.findByAuthor(user,pageable));

        return profileData;
    }

    public void processNaverLogin(String code, String state) {
        try {
            // 1. 네이버 토큰 요청 URL
            String tokenRequestUrl = "https://nid.naver.com/oauth2.0/token?"
                    + "grant_type=authorization_code"
                    + "&client_id=" + naverClientId
                    + "&client_secret=" + naverClientSecret
                    + "&code=" + code
                    + "&state=" + state;

            // 2. 토큰 요청
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> tokenResponse = restTemplate.getForEntity(tokenRequestUrl, Map.class);

            if (tokenResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> tokenData = tokenResponse.getBody();
                String accessToken = (String) tokenData.get("access_token");

                // 3. 사용자 정보 요청
                String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + accessToken);

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

                if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                    Map<String, Object> userInfoData = userInfoResponse.getBody();
                    Map<String, Object> response = (Map<String, Object>) userInfoData.get("response");

                    // 4. 사용자 정보 처리
                    String naverId = (String) response.get("id");
                    String email = (String) response.get("email");
                    String nickname = (String) response.get("nickname");

                    // 5. 사용자 DB에 저장 또는 기존 사용자 확인
                    SiteUser user = userRepository.findByNaverId(naverId)
                            .orElseGet(() -> {
                                // 새로운 사용자 생성
                                SiteUser newUser = new SiteUser();
                                newUser.setNaverId(naverId);
                                newUser.setUsername(nickname != null ? nickname : "naver_user_" + naverId);
                                newUser.setEmail(email);
                                return userRepository.save(newUser);
                            });

                    // 6. 사용자 로그인 처리 (세션 또는 SecurityContextHolder)
                    authenticateUser(user);
                } else {
                    throw new RuntimeException("사용자 정보 요청 실패");
                }
            } else {
                throw new RuntimeException("토큰 요청 실패");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("네이버 로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private void authenticateUser(SiteUser user) {
        // Spring Security 사용 시 SecurityContext에 인증 정보 설정
/*        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);*/

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
}
