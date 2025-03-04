package com.care.project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

    // 실제 서비스에서는 비밀키를 안전하게 관리할 것 (환경변수, 프로퍼티 등)
    private static final String SECRET_KEY = "your_secret_key_here";
    private static final long EXPIRATION_TIME = 86400000; // 1일 (밀리초)

    // JWT 생성: 사용자 id, username, email 포함
    public static String generateToken(String userId, String username, String email) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // JWT 검증 (필요 시 사용)
    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}