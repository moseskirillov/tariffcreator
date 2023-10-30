package com.fmlogistic.tariffcreator.jwt;

import com.fmlogistic.tariffcreator.services.user.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_HEADER = "Bearer ";
    private static final String AUTH = "Authorization";
    private static final String USER_AUTH_ERROR = "Can NOT set user authentication -> Message: {}";

    private final JwtProvider jwtProvider;
    private final UserDetailService userDetailService;

    @Override
    @SneakyThrows
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        try {
            var token = getToken(request);
            if (token != null) {
                var validToken = jwtProvider.validateAccessToken(token);
                if (validToken) {
                    var email = jwtProvider.getAccessClaims(token).getSubject();
                    var user = userDetailService.loadUserByUsername(email);
                    var authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (RuntimeException e) {
            logger.error(USER_AUTH_ERROR, e);
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        var authHeader = request.getHeader(AUTH);
        if (authHeader != null && authHeader.startsWith(BEARER_HEADER)) {
            return authHeader.replace(BEARER_HEADER, StringUtils.EMPTY);
        }
        return null;
    }
}