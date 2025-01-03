package com.mysite.sbb.user.service;

import com.mysite.sbb.user.entity.SessionUser;
import com.mysite.sbb.user.entity.SiteUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserService userService;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        SiteUser user = this.userService.socialLogin(registrationId,
                (String) attributes.get("name"), (String) attributes.get("email"));

        httpSession.setAttribute("user", new SessionUser(user));

        return new CustomOAuth2User(user.getUsername(), user.getEmail(), oAuth2User.getAttributes());
    }

    class CustomOAuth2User extends SiteUser implements OAuth2User {
        private Map<String, Object> attributes;

        public CustomOAuth2User(String username, String email, Map<String, Object> attributes) {
            super(username, email);
            this.attributes = attributes;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return this.attributes;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return authorities;
        }

        @Override
        public String getName() {
            return this.getUsername();
        }
    }
}
