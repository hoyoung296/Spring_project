package com.care.project.main.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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
//@RequestMapping("/login/oauth2/callback/kakao")
public class AuthController {

	@Autowired
	private AuthService authService;

	@GetMapping("/login/oauth2/callback/kakao")
	public ResponseEntity<LoginResponseDto> kakaoLogin(HttpServletRequest request) {
		// 카카오 서버가 전달한 인가 코드 추출
		String code = request.getParameter("code");
		System.out.println("코드 값 : " + code);

		// 1. 인가 코드를 이용해 카카오 토큰 발급 (전용 DTO에 매핑)
		KakaoTokenDto tokenDto = authService.getKakaoAccessToken(code);
		String kakaoAccessToken = tokenDto.getAccessToken();
		System.out.println("카카오 토큰 : " + kakaoAccessToken);

		// 2. 카카오 엑세스 토큰으로 카카오 사용자 정보 조회 후, UserDto로 변환하여 DB 저장 및 로그인 처리
		LoginResponseDto responseDto = authService.kakaoLogin(kakaoAccessToken);
		System.out.println("responseDto : " + responseDto);

		return ResponseEntity.ok(responseDto);
	}

	// 비밀번호 설정(또는 변경) 엔드포인트
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

	// Refresh Token 엔드포인트: Refresh Token을 이용해 새로운 Access Token 발급
	@GetMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
		try {
			String refreshToken = authHeader.replace("Bearer ", "");
			Claims claims = JwtUtil.validateToken(refreshToken);
			String newAccessToken = JwtUtil.generateToken(claims.getSubject(), (String) claims.get("username"),
					(String) claims.get("email"));
			Map<String, Object> tokens = new HashMap<>();
			tokens.put("jwtToken", newAccessToken);
			return ResponseEntity.ok(tokens);
		} catch (Exception e) {
			return ResponseEntity.status(401).body("Invalid or expired refresh token");
		}
	}

	// JWT를 이용해 사용자 정보 반환: 클라이언트가 Authorization 헤더에 JWT를 담아 요청
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