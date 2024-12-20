package com.mysite.sbb.oauth2;

import com.mysite.sbb.user.SiteUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class OAuth2UserInfo {
    private Map<String,Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;

    public static OAuth2UserInfo of(String registrationId,
                                    String userNameAttributeName,
                                    Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuth2UserInfo ofGoogle(String userNameAttributeName,
                                           Map<String,Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public SiteUser toSiteUser() {
        return SiteUser.builder()
                .name(this.name)
                .email(this.email)
                .build();
    }
}
