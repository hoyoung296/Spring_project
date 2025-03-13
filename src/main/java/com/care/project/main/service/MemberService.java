package com.care.project.main.service;

import com.care.project.main.dto.MemberDTO;

public interface MemberService {
	void registerMember(MemberDTO memberDTO);
    boolean loginMember(MemberDTO memberDTO);
    boolean updateMember(MemberDTO memberDTO);
    boolean deleteMember(String userId);
    MemberDTO getMember(String userId);
    boolean isUserIdDuplicate(String userId);  // 아이디 중복 체크
    boolean checkPassword(MemberDTO memberDTO); // 비밀번호 확인
    // 유효성 검사 메서드 추가
    boolean isUserIdValid(String userId);
    boolean isEmailValid(String email);
    boolean isPhoneNumberValid(String phoneNumber);
    boolean isPasswordValid(String password);
    
 // 아이디/비밀번호 찾기 기능 추가
    String findUserId(MemberDTO memberDTO);
    boolean findPasswordCheck(MemberDTO memberDTO);
    boolean updatePassword(MemberDTO memberDTO);
    
}