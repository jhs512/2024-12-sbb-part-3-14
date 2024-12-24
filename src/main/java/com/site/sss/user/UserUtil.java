package com.site.sss.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class UserUtil {

    public static String getUsernameFromAuthentication(Authentication authentication) {
        String username = "no user";

        if (authentication == null) {
            // 로그인하지 않은 경우
            return username;
        }

        if (authentication.getPrincipal() instanceof UserDetails) {
            // 일반 로그인 사용자
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            username = userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            // OAuth2 로그인 사용자
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            username = oauthUser.getAttribute("name"); // OAuth 제공자의 속성에 따라 다름
        }

        return username;
    }
}
