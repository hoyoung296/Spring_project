package com.care.project.main.mapper;

import java.util.List;

import com.care.project.main.dto.MemberDTO;

public interface MemberMapper {

	void register(MemberDTO memberDTO);

	void updateMember(MemberDTO memberDTO);

	void deleteMember(String userId);

	MemberDTO getMember(String userId);

	MemberDTO getMemberByEmail(String email); // 이메일 중복 체크

	List<MemberDTO> userData(); // 유저데이터 호출(관리자 페이지에서 사용)

	String findUserId(MemberDTO memberDTO); // 아이디 찾기

	int findPasswordCheck(MemberDTO memberDTO); // 비밀번호 찾기

	int updatePassword(MemberDTO memberDTO, String encodedPassword); // 임시 비밀번호 전송 및 DB업데이트

}