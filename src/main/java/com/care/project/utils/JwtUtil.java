package com.care.project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final String SECRET_KEY;
    private static final long EXPIRATION_TIME;
    private static final long REFRESH_EXPIRATION_TIME;

    static {
        Properties prop = new Properties();
        try (InputStream input = JwtUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IllegalArgumentException("application.properties 파일을 찾을 수 없습니다.");
            }
            prop.load(input);
            String secret = prop.getProperty("jwt.secret");
            String expiration = prop.getProperty("jwt.expiration");
            String refresh = prop.getProperty("refresh.expiration");

            if (secret == null || secret.trim().isEmpty()) {
                throw new IllegalArgumentException("jwt.secret 프로퍼티가 설정되어 있지 않습니다.");
            }
            SECRET_KEY = Base64.getEncoder().encodeToString(secret.getBytes());
            EXPIRATION_TIME = Long.parseLong(expiration.trim());
            REFRESH_EXPIRATION_TIME = Long.parseLong(refresh.trim());
        } catch (IOException e) {
            throw new RuntimeException("JWT 프로퍼티 로딩 중 오류 발생", e);
        }
    }

    public static String generateToken(String userId, String username, String email) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expirationDate = new Date(now + EXPIRATION_TIME);
        System.out.println("토큰 발급 시간 (iat): " + issuedAt);
        System.out.println("토큰 만료 시간 (exp): " + expirationDate);
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("email", email)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String generateRefreshToken(String userId, String username, String email) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expirationDate = new Date(now + REFRESH_EXPIRATION_TIME);
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("email", email)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setAllowedClockSkewSeconds(60)
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }
}
