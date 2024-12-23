package com.programmers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyRecommendedException extends RuntimeException {
    public AlreadyRecommendedException() {
        super("Already Recommended");
    }
}
