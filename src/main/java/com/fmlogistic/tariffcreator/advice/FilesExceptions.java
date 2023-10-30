package com.fmlogistic.tariffcreator.advice;

import com.fmlogistic.tariffcreator.exceptions.RejectFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FilesExceptions {

    private static final String REJECT_FILE_ERROR = "Ошибка отклонения файла: {}";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RejectFileException.class)
    public void rejectError(RejectFileException e) {
        log.error(REJECT_FILE_ERROR, e.getMessage());
    }

}
