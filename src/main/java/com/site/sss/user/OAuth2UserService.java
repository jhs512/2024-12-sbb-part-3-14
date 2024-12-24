package com.site.sss.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//OAuth 로그인시 자동으로 OAuth2UserService 실행
@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth 제공자 (Google, Naver, Kakao 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = SocialType.valueOf(registrationId.toUpperCase());

        // 사용자 정보 추출
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");


        // 사용자 정보 저장
        SiteUser user = userRepository.findByEmail(email)
                .orElseGet(() -> saveUser(email, name, socialType));

        return new CustomOAuth2User(oAuth2User);
    }



    private SiteUser saveUser(String email, String name, SocialType socialType) {
        SiteUser user = new SiteUser();
        user.setEmail(email);
        user.setUsername(name);

        user.setSocialType(socialType);

        return userRepository.save(user);
    }

}
