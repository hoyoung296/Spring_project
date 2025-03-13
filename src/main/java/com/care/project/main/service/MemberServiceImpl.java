package com.care.project.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.care.project.main.dto.LoginResponseDto;
import com.care.project.main.dto.MemberDTO;
import com.care.project.main.mapper.MemberMapper;
import com.care.project.utils.JwtUtil;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();// 비밀번호 암호화
    
    @Override
    public boolean isUserIdValid(String userId) {
        String userIdPattern = "^[a-zA-Z0-9]{6,}$";  // 아이디 : 6자 이상 영문자, 숫자만 포함
        return userId != null && userId.matches(userIdPattern);
    }

    @Override
    public boolean isEmailValid(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";  // 이메일 @ . 포함
        return email != null && email.matches(emailPattern);
    }

    @Override
    public boolean isPhoneNumberValid(String phoneNumber) {
        String phonePattern = "^\\d{10,11}$";  // 전화번호 형식: 숫자만 입력, 10~11자리 허용 (예: 01012345678)
        return phoneNumber != null && phoneNumber.matches(phonePattern);
    }

    @Override
    public boolean isPasswordValid(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$";  // 최소 8자, 영문자, 숫자, 특수문자 포함
        return password != null && password.matches(passwordPattern);
    }

    @Override
    public void registerMember(MemberDTO memberDTO) {
        if (memberDTO.getUserGrade() == null || memberDTO.getUserGrade().isEmpty()) {
            memberDTO.setUserGrade("일반");  // 기본 등급 설정
        }
        // 주소가 없다면 기본값을 설정하거나, 입력받은 값 그대로 사용
        if (memberDTO.getPostNum() == null || memberDTO.getPostNum().isEmpty()) {
            memberDTO.setPostNum("000000");  // 기본 우편번호
        }
        if (memberDTO.getAddr() == null || memberDTO.getAddr().isEmpty()) {
            memberDTO.setAddr("기본 주소");  // 기본 주소
        }
        if (memberDTO.getDetailAddr() == null || memberDTO.getDetailAddr().isEmpty()) {
            memberDTO.setDetailAddr("상세 주소");  // 기본 상세 주소
        }
        
        memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));// 비밀번호 암호화
        memberMapper.register(memberDTO);// 회원 등록
    }

    @Override
    public LoginResponseDto loginMember(MemberDTO memberDTO) {
        MemberDTO user = memberMapper.getMember(memberDTO.getUserId());// 회원 조회
        JwtUtil jwtUtil = new JwtUtil();
        String jwtToken = jwtUtil.generateToken(user.getUserId(), user.getUserName(), user.getEmail());
        String refreshToken = JwtUtil.generateRefreshToken(user.getUserId(), user.getUserName(), user.getEmail());
        
        // JWT 생성 (예: 사용자 id, username, email 포함)
        System.out.println("jwtToken : " + jwtToken);
        System.out.println("refreshToken : " + refreshToken);
        
        // 응답 DTO 구성
        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setLoginSuccess(true);
        responseDto.setJwtToken(jwtToken);
        responseDto.setRefreshToken(refreshToken);
        
        if(user != null && passwordEncoder.matches(memberDTO.getPassword(), user.getPassword())){
        	return responseDto;
        }
        return null;
        
        
    }

    @Override
    public boolean updateMember(MemberDTO memberDTO) {
        MemberDTO user = memberMapper.getMember(memberDTO.getUserId());
        if (user == null) return false; // 회원 존재 여부 확인

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(memberDTO.getPassword(), user.getPassword())) {
            return false; // 현재 비밀번호가 일치하지 않으면 false 반환
        }

        // 새로운 비밀번호가 입력된 경우 유효성 검사 추가
        if (memberDTO.getNewPassword() != null && !memberDTO.getNewPassword().isEmpty()) {
            if (!isPasswordValid(memberDTO.getNewPassword())) {
                return false; // 새로운 비밀번호가 유효성 검사에 실패하면 false 반환
            }
            memberDTO.setPassword(passwordEncoder.encode(memberDTO.getNewPassword())); // 새 비밀번호 암호화
        } else {
            // 새로운 비밀번호가 없으면 기존 비밀번호 유지
            memberDTO.setPassword(user.getPassword());
        }
        
        // 주소 정보 갱신
        if (memberDTO.getPostNum() == null || memberDTO.getPostNum().isEmpty()) {
            memberDTO.setPostNum(user.getPostNum());
        }
        if (memberDTO.getAddr() == null || memberDTO.getAddr().isEmpty()) {
            memberDTO.setAddr(user.getAddr());
        }
        if (memberDTO.getDetailAddr() == null || memberDTO.getDetailAddr().isEmpty()) {
            memberDTO.setDetailAddr(user.getDetailAddr());
        }

        // 변경하지 않은 값은 기존 값을 그대로 사용
        if (memberDTO.getUserName() == null || memberDTO.getUserName().isEmpty()) {
            memberDTO.setUserName(user.getUserName());
        }
        if (memberDTO.getEmail() == null || memberDTO.getEmail().isEmpty()) {
            memberDTO.setEmail(user.getEmail());
        }
        if (memberDTO.getPhoneNumber() == null || memberDTO.getPhoneNumber().isEmpty()) {
            memberDTO.setPhoneNumber(user.getPhoneNumber());
        }
        if (memberDTO.getPostNum() == null || memberDTO.getPostNum().isEmpty()) {
            memberDTO.setPostNum(user.getPostNum());
        }
        if (memberDTO.getAddr() == null || memberDTO.getAddr().isEmpty()) {
            memberDTO.setAddr(user.getAddr());
        }
        if (memberDTO.getDetailAddr() == null || memberDTO.getDetailAddr().isEmpty()) {
            memberDTO.setDetailAddr(user.getDetailAddr()); // 상세 주소를 기존 값으로 유지
        }
        if (memberDTO.getUserGrade() == null || memberDTO.getUserGrade().isEmpty()) {
            memberDTO.setUserGrade(user.getUserGrade());
        }
        if (memberDTO.getUserBirthday() == null) {
            memberDTO.setUserBirthday(user.getUserBirthday());
        }

        // 회원 정보 업데이트
        memberMapper.updateMember(memberDTO);
        return true;
    }

    @Override
    public boolean deleteMember(String userId) {
        if (memberMapper.getMember(userId) == null) return false;// 회원 존재 여부 확인
        memberMapper.deleteMember(userId);// 회원 삭제
        return true;
    }

    @Override
    public MemberDTO getMember(String userId) {
        return memberMapper.getMember(userId);// 회원 정보 조회
    }

    @Override
    public boolean isUserIdDuplicate(String userId) {
        return memberMapper.getMember(userId) != null;// 아이디 중복 체크
    }
    
    @Override
    public boolean isEmailDuplicate(String email) {
        return memberMapper.getMemberByEmail(email) != null;// 이메일 중복 체크
    }
    
    @Override
    public boolean checkPassword(MemberDTO memberDTO) {
        MemberDTO user = memberMapper.getMember(memberDTO.getUserId());
        return user != null && passwordEncoder.matches(memberDTO.getPassword(), user.getPassword());
    }
    
 // 아이디 찾기
    @Override
    public String findUserId(MemberDTO memberDTO) {
        return memberMapper.findUserId(memberDTO);
    }

    // 비밀번호 찾기 (사용자 확인)
    @Override
    public boolean findPasswordCheck(MemberDTO memberDTO) {
        return memberMapper.findPasswordCheck(memberDTO) > 0;
    }

    // 비밀번호 재설정
    @Override
    public boolean updatePassword(MemberDTO memberDTO) {
    	memberDTO.setNewPassword(passwordEncoder.encode(memberDTO.getNewPassword()));// 비밀번호 암호화
        return memberMapper.updatePassword(memberDTO) > 0;
    }
    
}