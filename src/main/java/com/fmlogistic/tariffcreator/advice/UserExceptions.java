package com.fmlogistic.tariffcreator.advice;

import com.fmlogistic.tariffcreator.exceptions.LoginException;
import com.fmlogistic.tariffcreator.exceptions.RefreshException;
import com.fmlogistic.tariffcreator.exceptions.RegisterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptions {

    private static final String USER_NOT_FOUND_ERROR = "Пользователь не найден: {}";
    private static final String USER_LOGIN_ERROR = "Ошибка входа в систему: {}";
    private static final String USER_REGISTER_ERROR = "Ошибка регистрации: {}";
    private static final String TOKEN_UPDATE_ERROR = "Пользователь не найден: {}";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UsernameNotFoundException.class)
    public void userNotFound(UsernameNotFoundException e) {
        log.error(USER_NOT_FOUND_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(LoginException.class)
    public void loginError(LoginException e) {
        log.error(USER_LOGIN_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(RegisterException.class)
    public String registerError(RegisterException e) {
        log.error(USER_REGISTER_ERROR, e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RefreshException.class)
    public void refreshTokenError(RefreshException e) {
        log.error(TOKEN_UPDATE_ERROR, e.getMessage());
    }

}