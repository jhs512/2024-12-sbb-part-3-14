package org.example.jtsb02.member.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.jtsb02.member.entity.Member;
import org.example.jtsb02.member.model.CustomOAuth2User;
import org.example.jtsb02.member.model.MemberRole;
import org.example.jtsb02.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String memberId = email.split("@")[0];

        return memberRepository.findByMemberIdAndEmail(memberId, email)
            .map(member -> {
                // 이미 존재하는 경우
                logger.info("User already exists with name = {} and email = {}", memberId, email);
                return new CustomOAuth2User(member.getId(), oAuth2User.getAuthorities(), oAuth2User.getAttributes(), "sub", member.getNickname(), member.getMemberId());
            })
            .orElseGet(() -> {
                // 존재하지 않으면 저장
                Member member = memberRepository.save(Member.builder()
                    .memberId(memberId)
                    .nickname(name)
                    .email(email)
                    .role(MemberRole.USER)
                    .build());
                logger.info("New user created with memberId = {} and email = {}", memberId, email);
                return new CustomOAuth2User(member.getId(), oAuth2User.getAuthorities(), oAuth2User.getAttributes(), "sub", member.getNickname(), member.getMemberId());
            });
    }
}
