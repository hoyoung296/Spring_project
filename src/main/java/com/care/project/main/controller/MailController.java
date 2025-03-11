package com.care.project.main.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.main.service.MailService;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/mail")
public class MailController {
    @Autowired
    private MailService mailService;

    // 이메일 인증번호 전송 (JSON body로 이메일 받기)
    @PostMapping("/send-auth-code")
    public ResponseEntity<ApiResponse> sendAuthCode(@RequestBody Map<String, String> requestData, HttpSession session) {
        String email = requestData.get("email");

        // 인증번호 생성 및 이메일 전송
        String authCode = mailService.sendAuthCode(email, session);

        if (authCode != null) {
            return ResponseEntity.ok(new ApiResponse("success", "인증번호가 이메일로 발송되었습니다.", authCode));
        } else {
            return ResponseEntity.status(500).body(new ApiResponse("error", "이메일 발송에 실패했습니다.", null));
        }
    }

 // 인증번호 검증
    @PostMapping("/verify-auth-code")
    public ResponseEntity<ApiResponse> verifyAuthCode(@RequestBody Map<String, String> requestData, HttpSession session) {
        String email = requestData.get("email");
        String code = requestData.get("code");

        // 세션에서 해당 이메일의 인증번호 가져오기
        String storedCode = (String) session.getAttribute("authCode:" + email);
        System.out.println("서버에서 저장된 인증번호: " + storedCode);  // 로그 확인
        System.out.println("사용자가 입력한 인증번호: " + code);  // 로그 확인

        if (storedCode != null && storedCode.equals(code)) {
            session.removeAttribute("authCode:" + email); // 인증 성공 후 세션에서 삭제
            return ResponseEntity.ok(new ApiResponse("success", "인증 성공", null));
        } else {
            return ResponseEntity.status(400).body(new ApiResponse("error", "인증 실패", null));
        }
    }

 // API 응답 형식 정의
    public static class ApiResponse {
        private String status;
        private String message;
        private String verificationCode; // 인증번호를 추가

        // 생성자에 verificationCode를 선택적으로 처리하도록 수정
        public ApiResponse(String status, String message, String verificationCode) {
            this.status = status;
            this.message = message;
            this.verificationCode = verificationCode;
        }

        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public String getVerificationCode() { return verificationCode; }
    }
}
