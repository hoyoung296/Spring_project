package com.care.project.main.service;

import com.care.project.main.dto.KakaoAccountDto;
import com.care.project.main.dto.KakaoTokenDto;
import com.care.project.main.dto.LoginResponseDto;
import com.care.project.main.dto.MemberDTO;
import com.care.project.main.mapper.MemberMapper;
import com.care.project.main.mapper.UserMapper;
import com.care.project.main.dto.UserDto;
import com.care.project.main.service.AuthService;
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
		// 1. 카카오 사용자 정보 조회 (전용 DTO에 매핑)
		KakaoAccountDto accountDto = getKakaoUserInfo(kakaoAccessToken);
		System.out.println("accountDto : " + accountDto);

		// 2. 생년월일 처리: birthyear와 birthday가 모두 존재하면 결합하여 Long으로 변환, 없으면 0L
		Long userBirthdayValue = 0L;
		String birthyear = accountDto.getKakao_account().getBirthyear();
		String birthday = accountDto.getKakao_account().getBirthday();
		if (birthyear != null && birthday != null) {
			try {
				userBirthdayValue = Long.parseLong(birthyear + birthday);
			} catch (NumberFormatException e) {
				// 변환 실패 시 로그 출력 후 기본값 0L 유지
				e.printStackTrace();
			}
		}

		// 카카오 사용자 정보 조회 후, 전화번호를 받아옴
		String rawPhoneNumber = accountDto.getKakao_account().getPhone_number();
		String formattedPhoneNumber = rawPhoneNumber;
		if (rawPhoneNumber != null && rawPhoneNumber.startsWith("+82 ")) {
			// "+82 " 접두사를 제거하고 그 자리에 "0"을 추가합니다.
			formattedPhoneNumber = "0" + rawPhoneNumber.substring(4);
		}

		// 3. 전용 DTO에서 필요한 필드 추출 → DB 저장용 DTO(UserDto)로 변환
		UserDto user = UserDto.builder().userId(accountDto.getKakao_account().getEmail())
				.username(accountDto.getKakao_account().getName()).email(accountDto.getKakao_account().getEmail())
				.password("") // 비밀번호는 소셜 로그인 시 사용하지 않음
				.phoneNumber(formattedPhoneNumber) // 카카오에서 받은 전화번호
				.addr("").postnum("").userBirthday(userBirthdayValue) // 변환된 생년월일 값
				.detailAddr("").userGrade("welcome").build();

//        // 4. DB에서 해당 사용자가 있는지 확인, 없으면 신규 회원 등록
//        UserDto existingUser = userMapper.selectUserById(user.getUserId());
//        if (existingUser == null) {
//        	 // 임의의 난수를 생성하고 해싱
//            String randomPassword = UUID.randomUUID().toString();
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            String encodedPassword = passwordEncoder.encode(randomPassword);
//            user.setPassword(encodedPassword);
//            userMapper.insertUser(user);
//        } else {
//            user = existingUser;
//        }

		boolean isNewUser = false;
		// 4. DB에서 해당 사용자가 있는지 확인, 없으면 신규 회원 등록
		UserDto existingUser = userMapper.selectUserById(user.getUserId());
		if (existingUser == null) {
			// 빈 문자열이 아닌, 임의의 난수를 생성하고 해싱한 값을 사용합니다.
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

		// 5. JWT 생성 (예: 사용자 id, username, email 포함)
		String jwtToken = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getEmail());
		String refreshToken = JwtUtil.generateRefreshToken(user.getUserId(), user.getUsername(), user.getEmail());
		System.out.println("jwtToken : " + jwtToken);
		System.out.println("refreshToken : " + refreshToken);

		// 6. 응답 DTO 구성
		LoginResponseDto responseDto = new LoginResponseDto();
		responseDto.setLoginSuccess(true);
		responseDto.setJwtToken(jwtToken);
		responseDto.setKakaoToken(kakaoAccessToken);
		responseDto.setRefreshToken(refreshToken);
		// 신규 회원이면, 프론트엔드에서 추가로 비밀번호 설정 페이지로 리디렉션할 수 있도록 플래그 전달
		responseDto.setNeedPasswordSetup(isNewUser);
		return responseDto;
	}

	@Override
	public void setPassword(String userId, String newPassword) {
		// DB에서 사용자 조회
		UserDto user = userMapper.selectUserById(userId);
		if (user == null) {
			throw new RuntimeException("존재하지 않는 사용자입니다.");
		}

		// 새 비밀번호 해싱 (BCrypt 사용 예시)
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(newPassword);

		// user에 새 비밀번호 설정 후 DB 업데이트
		user.setPassword(encodedPassword);
		// MyBatis Mapper에 updateUser 같은 메서드가 있어야 함
		userMapper.updateUser(user);

	}

	// 내부 메서드: 카카오 사용자 정보 조회
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