package com.mysite.sbb.global.util;

import lombok.Getter;

@Getter
public enum HttpMethod {
    DELETE("삭제"), MODIFY("수정");

    private String value;

    HttpMethod(String value) {
        this.value = value;
    }
}
