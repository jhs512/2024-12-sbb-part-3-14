package com.mysite.sbb.secure.Oauth2.service;

import com.mysite.sbb.secure.CustomOAuth2User;
import com.mysite.sbb.user.entity.SiteUser;
import com.mysite.sbb.user.entity.role.UserRole;
import com.mysite.sbb.user.repository.UserRepository;
import com.mysite.sbb.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;

@Service
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Oauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(oAuth2UserRequest);

        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String nameKey = "name";

        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            response.put("name", response.get("id"));
            attributes = response;
        } else if ("kakao".equals(registrationId)) {
            attributes = extractKakaoAttributes(attributes);
            nameKey = "nickname";
        }

        String name = String.valueOf(attributes.get("name"));
        System.out.println("attributes2 = " + attributes);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(name)) authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        else authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));

        DefaultOAuth2User user = new DefaultOAuth2User(authorities, attributes, nameKey);
        CustomOAuth2User customUser = new CustomOAuth2User(user);

        // SecurityContext에 사용자 정보 저장
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUser, // Principal
                null, // Credentials
                authorities // GrantedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SiteUser siteUser = userRepository.findByUsername(attributes.get("name").toString()).orElse(null);

        if (siteUser == null) {
            siteUser = SiteUser.builder()
                    .username(attributes.get("name").toString())
                    .password("")
                    .email(attributes.get("email").toString())
                    .build();

            userRepository.save(siteUser);
        }

        return customUser;
    }

    private Map<String, Object> extractKakaoAttributes(Map<String, Object> attributes) {
        System.out.println("attributes = " + attributes);
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        profile.put("email", kakaoAccount.getOrDefault("email","ohwoni1@daum.net"));
        profile.put("name", String.valueOf(attributes.get("id")));

        return profile;
    }

}