package com.care.project.main.service;

import com.care.project.main.dto.MemberDTO;

public interface MemberService {
	void registerMember(MemberDTO memberDTO);
    boolean loginMember(MemberDTO memberDTO);
    boolean updateMember(MemberDTO memberDTO);
    boolean deleteMember(String userId);
    MemberDTO getMember(String userId);
    boolean isUserIdDuplicate(String userId);  // 아이디 중복 체크
    boolean isEmailDuplicate(String email);  // 이메일 중복 체크
}
