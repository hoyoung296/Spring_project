package com.care.project.main.service;

import java.util.Random;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;

@Service
public class MailService {
    @Autowired 
    private JavaMailSender mailSender;

    // 6자리 인증번호 생성
    public String generateAuthCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // 0~9 랜덤 숫자
        }
        return code.toString();
    }

    // 이메일로 인증번호 전송
    public String sendAuthCode(String toEmail, HttpSession session) {
        String authCode = generateAuthCode(); // 인증번호 생성

        String subject = "<THEFILLM> 회원가입 인증번호 안내";
        String content = "<h3>THEFILLM을 찾아주셔서 감사합니다!</h3> <h1>" + authCode + "</h1>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            
            // 세션에 인증번호 저장
            session.setAttribute("authCode:" + toEmail, authCode);
            System.out.println("세션에 저장된 인증번호: " + session.getAttribute("authCode:" + toEmail));  // 세션 로그 추가
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return authCode;
    }
}
