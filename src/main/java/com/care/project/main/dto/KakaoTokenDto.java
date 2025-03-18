package com.care.project.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class KakaoTokenDto {
	// 카카오 토큰 API 응답을 매핑하는 DTO
	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("refresh_token")
	private String refreshToken;

	@JsonProperty("expires_in")
	private int expiresIn;

	@JsonProperty("refresh_token_expires_in")
	private int refreshTokenExpiresIn;

	private String scope;
}