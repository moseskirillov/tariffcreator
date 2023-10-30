package com.fmlogistic.tariffcreator.exceptions;

import com.fmlogistic.tariffcreator.models.user.response.RegisterResponse;

public class RegisterException extends RuntimeException {
    public RegisterException(RegisterResponse message) {
        super(String.valueOf(message));
    }

    public RegisterException(RuntimeException e, RegisterResponse message) {
        super(String.valueOf(message), e);
    }
}