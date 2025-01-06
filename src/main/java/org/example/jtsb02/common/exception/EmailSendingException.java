package org.example.jtsb02.common.exception;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to send temp password email")
public class EmailSendingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmailSendingException(String message, Throwable cause) {
        super(message);
    }
}
