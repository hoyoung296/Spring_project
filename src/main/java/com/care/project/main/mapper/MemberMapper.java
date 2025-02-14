package com.care.project.main.mapper;

import com.care.project.main.dto.MemberDTO;

public interface MemberMapper {
    void register(MemberDTO memberDTO);
    void updateMember(MemberDTO memberDTO);
    void deleteMember(String userId);
    MemberDTO getMember(String userId);
    MemberDTO getMemberByEmail(String email);  // 이메일 중복 체크
}