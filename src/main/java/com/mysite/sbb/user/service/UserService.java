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

    /*
    *
    *  2025-01-05 nickname 로직 개선
    *  userName의 경우는 로그인을 위한 ID로 이용하기 위한필드로 이용.
    *  nickname 필드를 추가하여 프로필 화면에서 OOO님 이라고 표시될 명칭을 username --> nickname으로 변경예정
    *
    *  그렇기 때문에 getCurrentUserName을 getCurrentUserName, getCurrentNickname으로 로직을 분리할 예정
    *
    *
    */
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

    /*
     *
     *  2025-01-05 nickname 로직 개선을 위해 추가한 닉네임 관련 매서드
     *
     */
    public String getNicknameByUsername(String username) {
        SiteUser siteUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return siteUser.getNickname() != null ? siteUser.getNickname() : "익명 사용자";
    }

    /*
    *  2025-01-06 : 임시 PW 발급 받은 유저인지 판별하는 로직
    *  기존 : userRepository에서 findByUsername 함수 이용해서 임시 PW를 발급받은 유저인지 확인
    *  수정 후 : getUserByUsername 동일 서비스 내의 함수를 이용 해서 유저 확인
    */
    public boolean isUsingTemporaryPassword(String username) {
        SiteUser user = this.getUserByUsername(username);

        boolean isTemp = user.isTempPassword();
        System.out.println("[LOG] 임시 비밀번호 여부: " + isTemp);
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

            //  기존 소스 depth가 너무 깊어!

            /*if (tokenResponse.getStatusCode() == HttpStatus.OK) {
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
                                newUser.setUsername("naver_user_" + naverId);
                                newUser.setNickname(nickname);
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
            }*/

            if (tokenResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("토큰 요청 실패");
            }

            // 3. 사용자 정보 요청
            Map<String, Object> tokenData = tokenResponse.getBody();
            String accessToken = (String) tokenData.get("access_token");

            String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

            if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("사용자 정보 요청 실패");
            }

            // 4. 사용자 정보 처리
            Map<String, Object> userInfoData = userInfoResponse.getBody();
            Map<String, Object> response = (Map<String, Object>) userInfoData.get("response");

            String naverId = (String) response.get("id");
            String email = (String) response.get("email");
            String nickname = (String) response.get("nickname");

            // 5. 사용자 DB에 저장 또는 기존 사용자 확인
            SiteUser user = userRepository.findByNaverId(naverId).orElseGet(() -> {
                SiteUser newUser = new SiteUser();
                newUser.setNaverId(naverId);
                newUser.setUsername(nickname != null ? nickname : "naver_user_" + naverId);
                newUser.setEmail(email);
                return userRepository.save(newUser);
            });

            // 6. 사용자 로그인 처리
            authenticateUser(user);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("네이버 로그인 처리 중 오류 발생: " + e.getMessage());
        }
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
