package org.example.jtsb02.member.model;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final Long id;
    private final String nickname;
    private final String memberId;

    public CustomOAuth2User(Long id, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey,
        String nickname, String memberId) {
        super(authorities, attributes, nameAttributeKey);
        this.id = id;
        this.nickname = nickname;
        this.memberId = memberId;
    }
}
