package com.care.project.main.service;

import com.care.project.main.dto.MemberDTO;

public interface MemberService {
	// 회원가입
    void registerMember(MemberDTO memberDTO);

    // 로그인
    MemberDTO loginMember(MemberDTO memberDTO);

    // 회원 정보 수정
    void updateMember(MemberDTO memberDTO);

    // 회원 탈퇴
    void deleteMember(String userId);

    // 특정 사용자 정보 조회
    MemberDTO getMemberInfo(String userId);
}
