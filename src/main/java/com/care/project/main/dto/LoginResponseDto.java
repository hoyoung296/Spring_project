package com.care.project.main.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private boolean loginSuccess;
    private String jwtToken;
    private String refreshToken;
    private String kakaoToken;
    private boolean needPasswordSetup;  // 신규 회원의 경우 비밀번호 설정 필요 여부 플래그
}