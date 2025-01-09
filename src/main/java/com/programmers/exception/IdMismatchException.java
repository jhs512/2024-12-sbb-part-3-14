package com.programmers.exception;

public class IdMismatchException extends RuntimeException {
    public IdMismatchException(String entityName) {
        super(String.format("Id mismatch: %s", entityName));
    }
}
