package com.ll.pratice1.domain.user.service;

import com.ll.pratice1.domain.user.SiteUser;
import com.ll.pratice1.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerTypeCode = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        String oauthId = oAuth2User.getName();
        String username = providerTypeCode + "__%s".formatted(oauthId);

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(UserRole.USER.getValue());

        SiteUser siteUser = userService.whenSocialLogin(providerTypeCode, username);

        // DB 저장로직이 필요하면 추가

        return new CustomOAuth2User(siteUser.getUsername(), siteUser.getPassword(), authorities);
    }
}

class CustomOAuth2User extends User implements OAuth2User {

    public CustomOAuth2User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}