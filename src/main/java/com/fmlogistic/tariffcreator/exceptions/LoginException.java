package com.fmlogistic.tariffcreator.exceptions;

public class LoginException extends RuntimeException {
    public LoginException(RuntimeException message) {
        super(message);
    }
}
