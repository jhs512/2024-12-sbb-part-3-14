package com.mysite.sbb.web.api.common;

public record ApiResponse(boolean success, String message, Object data) {

    public ApiResponse(boolean success, String message) {
        this(success, message, null);
    }
}
