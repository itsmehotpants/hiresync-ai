package com.hiresync.ai.auth;

import com.hiresync.common.exception.HireSyncException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class AiJwtService {

    private final SecretKey signingKey;

    public AiJwtService(@Value("${app.jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserIdFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            throw HireSyncException.badRequest("Missing authorization token");
        }
        return extractUserIdFromToken(bearerToken.substring(7));
    }

    public String extractUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (claims.getExpiration().before(new Date())) {
                throw HireSyncException.badRequest("Token expired");
            }
            return claims.getSubject();
        } catch (JwtException e) {
            throw HireSyncException.badRequest("Invalid token");
        }
    }
}
