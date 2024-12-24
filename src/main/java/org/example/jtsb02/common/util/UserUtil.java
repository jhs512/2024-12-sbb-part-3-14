package org.example.jtsb02.common.util;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserUtil {

    // 사용자 이름 꺼내오는 메서드
    public static String getUsernameFromPrincipal(Principal principal) {
        return principal.getName();
    }

    // 권한 체크 메서드
    public static void checkUserPermission(String principalUsername, String targetUsername, String action) {
        if(!principalUsername.equals(targetUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, action + " 권한이 없습니다.");
        }
    }
}
