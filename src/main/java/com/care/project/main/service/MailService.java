package com.care.project.main.service;

import java.util.Random;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired 
    private JavaMailSender mailSender;

    // 6자리 인증번호 생성
    private String generateAuthCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 이메일 전송 및 인증번호 반환
    public String sendAuthCode(String toEmail) {
        String authCode = generateAuthCode();

        String subject = "<THEFILLM> 회원가입 인증번호 안내";
        String content = "<h3>THEFILLM을 찾아주셔서 감사합니다!</h3> <h1>" + authCode + "</h1>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return authCode;
    }
}
