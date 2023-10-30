package com.fmlogistic.tariffcreator.jwt;

import com.fmlogistic.tariffcreator.models.user.AccessTokenModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private static final String ROLES = "roles";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String TOKEN_EXPIRED_ERROR = "Token expired";
    private static final String UNSUPPORTED_JWT_ERROR = "Unsupported jwt";
    private static final String MALFORMED_JWT_ERROR = "Malformed jwt";
    private static final String INVALID_SIGNATURE_ERROR = "Invalid signature";
    private static final String INVALID_TOKEN_ERROR = "Invalid token";

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
        @Value("${jwt.secret.access}") String jwtAccessSecret,
        @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(@NonNull AccessTokenModel tokenModel) {
        final var now = LocalDateTime.now();
        final var accessExpirationInstant = now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant();
        final var accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
            .setSubject(tokenModel.email())
            .setExpiration(accessExpiration)
            .signWith(jwtAccessSecret)
            .claim(ROLES, tokenModel.roles())
            .claim(FIRST_NAME, tokenModel.firstName())
            .claim(LAST_NAME, tokenModel.lastName())
            .compact();
    }

    public String generateRefreshToken(@NonNull String email) {
        return Jwts.builder()
            .setSubject(email)
            .setExpiration(Date.from(
                LocalDateTime.now()
                    .plusDays(30)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()))
            .signWith(jwtRefreshSecret)
            .compact();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token, SecretKey jwtSecret) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error(TOKEN_EXPIRED_ERROR, expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error(UNSUPPORTED_JWT_ERROR, unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error(MALFORMED_JWT_ERROR, mjEx);
        } catch (SignatureException sEx) {
            log.error(INVALID_SIGNATURE_ERROR, sEx);
        } catch (Exception e) {
            log.error(INVALID_TOKEN_ERROR, e);
        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    public Claims getClaims(String token, SecretKey secret) {
        return Jwts.parserBuilder()
            .setSigningKey(secret)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

}
