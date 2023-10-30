package com.fmlogistic.tariffcreator.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final String AUTHORIZED_ERROR = "Unauthorized error. Message - {}";
    private static final String INVALID_CRED_ERROR = "Invalid credentials";

    @Override
    @SneakyThrows
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        log.error(AUTHORIZED_ERROR, authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_CRED_ERROR);
    }
}