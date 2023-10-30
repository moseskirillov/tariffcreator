package com.fmlogistic.tariffcreator.advice;

import com.fmlogistic.tariffcreator.exceptions.MailjetException;
import com.fmlogistic.tariffcreator.exceptions.RefreshException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ServerExceptions {

    private static final String INTERNAL_SERVER_ERROR = "Внутреняя ошибка сервера: {}";
    private static final String MAILJET_SEND_ERROR = "Ошибка отправки письма: {}";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MailjetException.class)
    public void mailjetError(MailjetException e) {
        log.error(MAILJET_SEND_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public void internalError(RefreshException e) {
        log.error(INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
