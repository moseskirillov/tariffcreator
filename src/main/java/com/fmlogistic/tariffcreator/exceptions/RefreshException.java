package com.fmlogistic.tariffcreator.exceptions;

public class RefreshException extends RuntimeException {
    public RefreshException(RuntimeException e) {
        super(e);
    }

    public RefreshException(String message) {
        super(message);
    }
}
