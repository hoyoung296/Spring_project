package com.care.project.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommonResponse<T> {
	private int code;
	private String message;
	private T data;

	public CommonResponse(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public CommonResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static ResponseEntity<?> createResponse(CommonResponse response, HttpStatus httpStatus) {
		return new ResponseEntity<>(response, httpStatus);
	}
}
