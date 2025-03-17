package com.care.project.main.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.care.project.main.dto.KakaoTokenDto;
import com.care.project.main.dto.LoginResponseDto;
import com.care.project.main.dto.PasswordRequestDto;
import com.care.project.main.service.AuthService;
import com.care.project.utils.JwtUtil;

import io.jsonwebtoken.Claims;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("member")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 로그인 (카카오 로그인 후) → Access Token은 JSON, Refresh Token은 쿠키로 전달
    @GetMapping("/login/oauth2/callback/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(
            HttpServletRequest request,
            HttpServletResponse response) {
        String code = request.getParameter("code");
        System.out.println("코드 값 : " + code);

        // 1. 카카오 토큰 발급 및 사용자 정보 처리
        KakaoTokenDto tokenDto = authService.getKakaoAccessToken(code);
        String kakaoAccessToken = tokenDto.getAccessToken();
        System.out.println("카카오 토큰 : " + kakaoAccessToken);

        // 2. 로그인 처리 (DB 저장, JWT 생성 등)
        LoginResponseDto responseDto = authService.kakaoLogin(kakaoAccessToken);
        System.out.println("responseDto : " + responseDto);

        // 3. Refresh Token을 HttpOnly, Secure 쿠키로 설정
        String refreshToken = responseDto.getRefreshToken();
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // HTTPS 환경에서만 사용
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 (초 단위)
        response.addCookie(refreshCookie);

        // 4. JSON 응답에서는 refreshToken 제거 (보안 강화)
        responseDto.setRefreshToken(null);

        return ResponseEntity.ok(responseDto);
    }

    // 비밀번호 설정 (또는 변경) 엔드포인트
    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestBody PasswordRequestDto requestDto) {
        try {
            authService.setPassword(requestDto.getUserId(), requestDto.getPassword());
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // Refresh Token 재발급 엔드포인트 → 쿠키에서 refreshToken을 읽음
    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        try {
            if (refreshToken == null) {
                return ResponseEntity.status(401).body("No refresh token cookie found");
            }
            Claims claims = JwtUtil.validateToken(refreshToken);
            String newAccessToken = JwtUtil.generateToken(
                    claims.getSubject(),
                    (String) claims.get("username"),
                    (String) claims.get("email"));
            Map<String, Object> tokens = new HashMap<>();
            tokens.put("jwtToken", newAccessToken);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }
    }

    // 사용자 정보 반환 (Authorization 헤더의 Access Token 사용)
    @GetMapping("user/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        System.out.println("실행!!!!");
        try {
            String token = authHeader.replace("Bearer ", "");
            Claims claims = JwtUtil.validateToken(token);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", claims.getSubject());
            userInfo.put("username", claims.get("username"));
            userInfo.put("email", claims.get("email"));
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }
}
