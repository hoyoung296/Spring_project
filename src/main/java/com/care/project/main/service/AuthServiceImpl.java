package com.care.project.main.service;

import com.care.project.main.dto.KakaoAccountDto;
import com.care.project.main.dto.KakaoTokenDto;
import com.care.project.main.dto.LoginResponseDto;
import com.care.project.main.dto.UserDto;
import com.care.project.main.mapper.UserMapper;
import com.care.project.utils.JwtUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public KakaoTokenDto getKakaoAccessToken(String code) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = rt.exchange(kakaoTokenUri, HttpMethod.POST, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoTokenDto tokenDto = null;
        try {
            tokenDto = mapper.readValue(response.getBody(), KakaoTokenDto.class);
            System.out.println("카카오토큰dto : " + tokenDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokenDto;
    }

    @Override
    public LoginResponseDto kakaoLogin(String kakaoAccessToken) {
        KakaoAccountDto accountDto = getKakaoUserInfo(kakaoAccessToken);
        System.out.println("accountDto : " + accountDto);

        UserDto user = UserDto.builder()
                .userId(accountDto.getKakao_account().getEmail())
                .username(accountDto.getKakao_account().getName())
                .email(accountDto.getKakao_account().getEmail())
                .password("")
                .phoneNumber(accountDto.getKakao_account().getPhone_number())
                .addr("").postnum("").userBirthday(0L)
                .detailAddr("").userGrade("welcome").build();

        boolean isNewUser = false;
        UserDto existingUser = userMapper.selectUserById(user.getUserId());
        if (existingUser == null) {
            String randomPassword = UUID.randomUUID().toString();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(randomPassword);
            user.setPassword(encodedPassword);
            isNewUser = true;
            userMapper.insertUser(user);
        } else {
            user = existingUser;
        }

        JwtUtil jwtUtil = new JwtUtil();
        String jwtToken = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getEmail());
        String refreshToken = JwtUtil.generateRefreshToken(user.getUserId(), user.getUsername(), user.getEmail());
        System.out.println("jwtToken : " + jwtToken);
        System.out.println("refreshToken : " + refreshToken);

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setLoginSuccess(true);
        responseDto.setJwtToken(jwtToken);
        responseDto.setKakaoToken(kakaoAccessToken);
        responseDto.setRefreshToken(refreshToken);  // Controller에서 쿠키로 전송 후 null 처리
        responseDto.setNeedPasswordSetup(isNewUser);
        return responseDto;
    }

    @Override
    public void setPassword(String userId, String newPassword) {
        UserDto user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userMapper.updateUser(user);
    }

    private KakaoAccountDto getKakaoUserInfo(String kakaoAccessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(kakaoUserInfoUri, HttpMethod.POST, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoAccountDto accountDto = null;
        try {
            accountDto = mapper.readValue(response.getBody(), KakaoAccountDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountDto;
    }
}
