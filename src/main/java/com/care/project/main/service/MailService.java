package com.care.project.main.service;

import java.security.SecureRandom;
import java.util.Random;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.care.project.main.dto.MemberDTO;
import com.care.project.main.mapper.MemberMapper;

@Service
public class MailService {
    @Autowired 
    private JavaMailSender mailSender;
    
    @Autowired
    private MemberMapper membermapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();// 비밀번호 암호화
    
    private static final String PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_MIN_LENGTH = 8;  // 최소 8자
    private static final int PASSWORD_MAX_LENGTH = 12; // 최대 12자
    

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
    
    //임시 비밀번호 발송 + DB업데이트
    public boolean sendAuthPassword(MemberDTO memberDTO) {
    	String tempPassword = generateTempPassword();

        String subject = "<THEFILLM> 임시 비밀번호 발송 완료";
        String content = "<h3>THEFILLM 임시 비밀번호 안내</h3> <h1>" + tempPassword + "</h1>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(memberDTO.getUserId());
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            
         // 임시 비밀번호를 암호화하여 DB에 저장
            String encodedPassword = passwordEncoder.encode(tempPassword);
            int result = membermapper.updatePassword(memberDTO, encodedPassword);
            return result > 0; // 성공 여부 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /*
    @Override
	public boolean isPasswordValid(String password) {
		String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$"; // 최소 8자, 영문자, 숫자, 특수문자 포함
		return password != null && password.matches(passwordPattern);
	}
	*/
 // 랜덤한 임시 비밀번호 생성 (유효성 조건 충족)
    private String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        int passwordLength = PASSWORD_MIN_LENGTH + random.nextInt(PASSWORD_MAX_LENGTH - PASSWORD_MIN_LENGTH + 1); // 8~12자 랜덤

        while (true) {
            password.setLength(0);
            boolean hasLetter = false, hasDigit = false, hasSpecial = false;

            for (int i = 0; i < passwordLength; i++) {
                char c = PASSWORD_CHARACTERS.charAt(random.nextInt(PASSWORD_CHARACTERS.length()));
                password.append(c);
                if (Character.isLetter(c)) hasLetter = true;
                else if (Character.isDigit(c)) hasDigit = true;
                else hasSpecial = true;
            }

            // 조건을 만족하면 반환
            if (hasLetter && hasDigit && hasSpecial) {
                return password.toString();
            }
        }
    }
    
}
