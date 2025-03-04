package com.care.project.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAccountDto {
    private Long id;
    private KakaoAccount kakao_account;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        private String email;
        private String name;
        private String birthday;
        private String birthyear;
        private String phone_number;
    }
}