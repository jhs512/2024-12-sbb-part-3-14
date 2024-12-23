package com.programmers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundDataException extends RuntimeException {
    public NotFoundDataException(String message) {
        super(message);
    }
}
