package com.catcoffee.backend.security;

import com.catcoffee.backend.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenProvider {

    public static final String ACCESS_TOKEN = "access";
    public static final String REFRESH_TOKEN = "refresh";

    private final SecurityProperties securityProperties;
    private SecretKey secretKey;

    public JwtTokenProvider(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(securityProperties.getJwtSecret());
        } catch (Exception ex) {
            keyBytes = securityProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(AuthUser authUser) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusHours(securityProperties.getAccessTokenExpireHours());
        return buildToken(authUser, now, expireAt, ACCESS_TOKEN);
    }

    public String generateRefreshToken(AuthUser authUser) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusDays(securityProperties.getRefreshTokenExpireDays());
        return buildToken(authUser, now, expireAt, REFRESH_TOKEN);
    }

    private String buildToken(AuthUser authUser, LocalDateTime now, LocalDateTime expireAt, String tokenType) {
        return Jwts.builder()
                .subject(authUser.getUsername())
                .claim("uid", authUser.getId())
                .claim("nickname", authUser.getNickname())
                .claim("roles", authUser.getRoles())
                .claim("permissions", authUser.getPermissions())
                .claim("tokenVersion", authUser.getTokenVersion())
                .claim("tokenType", tokenType)
                .issuedAt(toDate(now))
                .expiration(toDate(expireAt))
                .signWith(secretKey)
                .compact();
    }

    public AuthUser parseToken(String token) {
        Claims claims = parseClaims(token);
        return AuthUser.builder()
                .id(((Number) claims.get("uid")).longValue())
                .username(claims.getSubject())
                .nickname((String) claims.get("nickname"))
                .roles(claims.get("roles", List.class))
                .permissions(claims.get("permissions", List.class))
                .tokenVersion(((Number) claims.get("tokenVersion")).intValue())
                .status(1)
                .build();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public boolean isValid(String token, String tokenType) {
        try {
            Claims claims = parseClaims(token);
            return tokenType.equals(claims.get("tokenType", String.class));
        } catch (Exception ex) {
            return false;
        }
    }

    public Map<String, Object> parseTokenMeta(String token) {
        Claims claims = parseClaims(token);
        return Map.of(
                "uid", ((Number) claims.get("uid")).longValue(),
                "tokenVersion", ((Number) claims.get("tokenVersion")).intValue(),
                "tokenType", claims.get("tokenType", String.class)
        );
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
