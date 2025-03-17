package com.care.project.main.service;

import com.care.project.main.dto.KakaoTokenDto;
import com.care.project.main.dto.LoginResponseDto;

public interface AuthService {
    // 인가 코드를 이용해 카카오 토큰을 받아옴
    KakaoTokenDto getKakaoAccessToken(String code);
    
    // 카카오 엑세스 토큰으로 사용자 정보를 조회하고,
    // 전용 DTO에서 필요한 정보를 추출하여 UserDto로 변환 후 DB에 저장하거나 업데이트하고 로그인 처리
    LoginResponseDto kakaoLogin(String kakaoAccessToken);
    
    void setPassword(String userId, String newPassword);
}