package org.example.jtsb02.common.util;

import java.security.Principal;
import org.example.jtsb02.member.model.CustomOAuth2User;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.server.ResponseStatusException;

public class UserUtil {

    // 사용자 이름 꺼내오는 메서드
    public static String getUsernameFromPrincipal(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();

            // CustomOAuth2User 로 캐스팅
            if (oauth2User instanceof CustomOAuth2User customOAuth2User) {
                return customOAuth2User.getMemberId();
            }
        }
        return principal.getName();
    }

    // 권한 체크 메서드
    public static void checkUserPermission(String principalUsername, String targetUsername, String action) {
        if(!principalUsername.equals(targetUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, action + " 권한이 없습니다.");
        }
    }
}
