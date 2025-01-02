package org.example.jtsb02.common.exception;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
public class PasswordNotMatchException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    public PasswordNotMatchException(String message) {
        super(message);
    }
}
