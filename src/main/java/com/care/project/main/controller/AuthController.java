package com.care.project.main.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.care.project.main.dto.KakaoTokenDto;
import com.care.project.main.dto.LoginResponseDto;
import com.care.project.main.service.AuthService;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
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
        System.out.println("카카오 토큰 : "+kakaoAccessToken);

        // 2. 카카오 엑세스 토큰으로 카카오 사용자 정보 조회 후, UserDto로 변환하여 DB 저장 및 로그인 처리
        LoginResponseDto responseDto = authService.kakaoLogin(kakaoAccessToken);
        System.out.println("responseDto : " + responseDto);
        
        return ResponseEntity.ok(responseDto);
    }
}
