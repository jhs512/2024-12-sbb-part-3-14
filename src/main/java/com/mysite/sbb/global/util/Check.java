package com.mysite.sbb.global.util;

import com.mysite.sbb.answer.entity.Answer;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.user.entity.SiteUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class Check {
    public static void permission(Object object, String username, HttpMethod httpMethod) {
        SiteUser author;

        if(object instanceof Question) {
            Question question = (Question) object;
            author = question.getAuthor();
        } else if(object instanceof Answer) {
            Answer answer = (Answer) object;
            author = answer.getAuthor();
        } else {
            throw new IllegalArgumentException("잘못된 데이터 타입입니다.");
        }

        if(!author.getUsername().equals(username)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "%s권한이 없습니다.".formatted(httpMethod.getValue()));
        }
    }
}
