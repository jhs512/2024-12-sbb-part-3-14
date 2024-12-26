package com.mysite.sbb.web.common.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SecurityValidaotr {

    /**
     * 사용자 권한 검증 메서드
     * @param currentUsername 현재 사용자 이름
     * @param authorUsername 작성자 이름
     * @param actionMessage 액션 메시지 (예: "수정권한", "삭제권한")
     */
    public static void validateUserPermission(String currentUsername, String authorUsername, String actionMessage) {
        if (!currentUsername.equals(authorUsername)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, actionMessage + "이(가) 없습니다.");
        }
    }

}
