package com.mysite.sbb.secure;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;

    public CustomOAuth2User(OAuth2User oauth2User) {
        this.oauth2User = oauth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return (String) oauth2User.getAttributes().get("name");// 기본 이름 속성
    }

    // 커스텀 메서드 추가
    public String getEmail() {
        return oauth2User.getAttribute("email");
    }

    public String getUsername() {
        return oauth2User.getAttribute("name"); // 사용자 이름
    }
}
