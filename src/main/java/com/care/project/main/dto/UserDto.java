package com.care.project.main.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
	private String userId; // 카카오 id (문자열 변환)
	private String username; // 카카오 닉네임
	private String password; // 빈 문자열 (암호화 로직 추가 가능)
	private String email;
	private String phoneNumber;
	private String addr;
	private String postnum;
	private Long userBirthday;
	private String detailAddr;
	private String userGrade; // 기본값 "welcome"
}