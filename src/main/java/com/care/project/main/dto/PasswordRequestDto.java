package com.care.project.main.dto;

import lombok.Data;

@Data
public class PasswordRequestDto {
    private String userId;    // 이메일 또는 DB에 저장된 user_id
    private String password;  // 새로 설정할 비밀번호
}