package com.care.project.main.mapper;

import com.care.project.main.dto.MemberDTO;

public interface MemberMapper {
	// 회원가입
    void register(MemberDTO memberDTO);

    // 로그인
    MemberDTO login(MemberDTO memberDTO);

    // 회원 정보 수정
    void updateMember(MemberDTO memberDTO);

    // 회원 탈퇴
    void deleteMember(String userId);

    // 특정 사용자 정보 조회
    MemberDTO getMember(String userId);
}
