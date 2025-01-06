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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

import java.security.Principal;
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


    public SiteUser create(String username, String email,String password,String nickname) {
        SiteUser siteUser = new SiteUser();
        siteUser.setUsername(username);
        siteUser.setEmail(email);
        siteUser.setNickname(nickname);
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);

        return siteUser;
    }

    public String getCurrentUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("[DEBUG] principal 타입: " + principal.getClass().getName());
        System.out.println("[DEBUG] principal 값: " + principal);

        if (principal instanceof SiteUser) {
            return ((SiteUser) principal).getUsername();
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            return ((org.springframework.security.core.userdetails.User) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        } else {
            return  "guest";
        }
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);

        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("User not found");
        }
    }

    public String getNicknameByUsername(String username) {
        SiteUser siteUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return siteUser.getNickname() != null ? siteUser.getNickname() : "익명 사용자";
    }

    public boolean isUsingTemporaryPassword(String username) {
        SiteUser user = this.getUserByUsername(username);

        boolean isTemp = user.isTempPassword();

        return isTemp;
    }

    public SiteUser getUserByUsername(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username.toLowerCase());
        return siteUser.orElseThrow(() -> new DataNotFoundException("User not found: " + username));
    }

    public Map<String,Object> getUserProfile(String username, String nickname, Pageable pageable) {
        SiteUser user = this.getUser(username);

        Map<String,Object> profileData = new HashMap<>();
        profileData.put("user", user);
        profileData.put("nickname", nickname);
        profileData.put("questions",questionRepository.findByAuthor(user,pageable));
        profileData.put("answers",answerRepository.findByAuthor(user,pageable));
        profileData.put("comments",commentRepository.findByAuthor(user,pageable));

        return profileData;
    }

    public void processNaverLogin(String code, String state) {
        try {
            // 1.  토큰 발급
            String accessToken = getAccessToken(code, state);

            // 2. 사용자 정보 가져오기
            Map<String, Object> userInfo = getUserInfo(accessToken);

            // 3. 사용자 정보 처리 및 저장/업데이트
            SiteUser user = saveOrUpdateUser(userInfo);

            // 4. 사용자 인증 처리
            authenticateUser(user);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("네이버 로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private String getAccessToken(String code, String state) {
        String tokenRequestUrl = "https://nid.naver.com/oauth2.0/token?"
                + "grant_type=authorization_code"
                + "&client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
                + "&code=" + code
                + "&state=" + state;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> tokenResponse = restTemplate.getForEntity(tokenRequestUrl, Map.class);

        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("토큰 요청 실패");
        }

        Map<String, Object> tokenData = tokenResponse.getBody();
        return (String) tokenData.get("access_token");
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("사용자 정보 요청 실패");
        }

        Map<String, Object> userInfoData = userInfoResponse.getBody();
        return (Map<String, Object>) userInfoData.get("response");
    }

    private SiteUser saveOrUpdateUser(Map<String, Object> userInfo) {
        String naverId = (String) userInfo.get("id");
        String email = (String) userInfo.get("email");
        String nickname = (String) userInfo.get("nickname");

        return userRepository.findByNaverId(naverId)
                             .orElseGet(() -> {
                                SiteUser newUser = new SiteUser();
                                newUser.setNaverId(naverId);
                                newUser.setUsername("naver_"+naverId);
                                newUser.setEmail(email);
                                newUser.setNickname(nickname);
                                return userRepository.save(newUser);
                             });

    }




    private void authenticateUser(SiteUser user) {
        System.out.println("[LOG] authenticateUser() 호출 - user: " + user.getUsername());
        // Spring Security 사용 시 SecurityContext에 인증 정보 설정
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        System.out.println("[LOG] 인증 성공 - SecurityContextHolder 업데이트 완료");
    }
}
