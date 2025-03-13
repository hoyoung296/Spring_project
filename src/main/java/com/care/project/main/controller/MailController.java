package com.care.project.main.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import com.care.project.main.service.MailService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/mail")
@SessionAttributes("authCode") // 세션에 authCode 저장
public class MailController {

    @Autowired
    private MailService mailService;

    // 이메일 인증번호 전송
    @PostMapping("/send-auth-code")
    public ResponseEntity<Object> sendAuthCode(@RequestBody Map<String, String> requestData, HttpSession session) {
        String email = requestData.get("email");
        System.out.println("이메일로 받은 값: " + email);

        String authCode = mailService.sendAuthCode(email);
        session.setAttribute("authCode", authCode); // 세션에 인증번호 저장

        System.out.println("📌 send-auth-code - 세션 ID: " + session.getId());
        System.out.println("서버에서 생성한 인증번호: " + authCode);
        System.out.println("세션에 저장된 인증번호: " + session.getAttribute("authCode"));

        if (authCode != null) {
            return ResponseEntity.ok().body(new ApiResponse("success", "인증번호가 이메일로 발송되었습니다.", authCode));
        } else {
            return ResponseEntity.status(500).body(new ApiResponse("error", "이메일 발송에 실패했습니다."));
        }
    }

    // 인증번호 확인
    @PostMapping("/verify-auth-code")
    public ResponseEntity<Object> verifyAuthCode(@RequestBody Map<String, String> requestData, HttpSession session, SessionStatus status) {
        System.out.println("📌 verify-auth-code - 세션 ID: " + session.getId());
        System.out.println("Request data: " + requestData);  // 추가된 로그

        String code = requestData.get("verificationCode");
        String storedCode = (String) session.getAttribute("authCode");

        System.out.println("사용자가 입력한 인증번호: " + code);
        System.out.println("서버에서 저장된 인증번호: " + storedCode);

        if (storedCode != null && storedCode.equals(code)) {
            status.setComplete(); // 세션에서 authCode 삭제
            return ResponseEntity.ok().body(new ApiResponse("success", "인증 성공"));
        } else {
            return ResponseEntity.status(400).body(new ApiResponse("error", "인증 실패"));
        }
    }

    // ApiResponse 클래스 수정
    public static class ApiResponse {
        private String status;
        private String message;
        private String verificationCode;  // 인증번호 추가

        public ApiResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public ApiResponse(String status, String message, String verificationCode) {
            this.status = status;
            this.message = message;
            this.verificationCode = verificationCode;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getVerificationCode() {
            return verificationCode;
        }
    }
}
