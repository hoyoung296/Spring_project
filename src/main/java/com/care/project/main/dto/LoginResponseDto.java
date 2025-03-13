package com.care.project.main.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private boolean loginSuccess;
    private String jwtToken;
    private String refreshToken;
}