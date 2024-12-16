package com.example.article_site.oauth;

import com.example.article_site.domain.Author;
import com.example.article_site.security.UserRole;
import com.nimbusds.oauth2.sdk.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String nickname;  // username이 아닌 nickname
    private String email;
    private String picture;
    private String provider;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String nameAttributeKey,
                           String nickname,
                           String email,
                           String picture,
                           String provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.nickname = nickname;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
    }

    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String id = attributes.get("id").toString();
        String tempEmail = "kakao_" + id + "@temp.com";

        return OAuthAttributes.builder()
                .nickname((String) profile.get("nickname"))  // username이 아닌 nickname
                .email(tempEmail)
                .picture((String) profile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider("kakao")
                .build();
    }

    public Author toEntity() {
        return Author.createSocialAuthor(
                nickname,
                email,
                picture,
                provider,
                attributes.get("id").toString()
        );
    }
}