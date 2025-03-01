package com.care.project.main.mapper;

import java.util.List;

import com.care.project.main.dto.MemberDTO;

public interface MemberMapper {
    void register(MemberDTO memberDTO);
    void updateMember(MemberDTO memberDTO);
    void deleteMember(String userId);
    MemberDTO getMember(String userId);
    MemberDTO getMemberByEmail(String email);  // 이메일 중복 체크
    List<MemberDTO> userData(); //유저데이터 호출(관리자 페이지에서 사용)
}