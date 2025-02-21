package com.care.project.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.care.project.main.dto.MemberDTO;
import com.care.project.main.mapper.MemberMapper;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();// 비밀번호 암호화

    @Override
    public void registerMember(MemberDTO memberDTO) {
        if (memberDTO.getUserGrade() == null || memberDTO.getUserGrade().isEmpty()) {
            memberDTO.setUserGrade("일반");  // 기본 등급 설정
        }
        memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));// 비밀번호 암호화
        memberMapper.register(memberDTO);// 회원 등록
    }

    @Override
    public boolean loginMember(MemberDTO memberDTO) {
        MemberDTO user = memberMapper.getMember(memberDTO.getUserId());// 회원 조회
        return user != null && passwordEncoder.matches(memberDTO.getPassword(), user.getPassword());// 비밀번호 검증
    }

    @Override
    public boolean updateMember(MemberDTO memberDTO) {
        MemberDTO user = memberMapper.getMember(memberDTO.getUserId());
        if (user == null) return false; // 회원 존재 여부 확인

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(memberDTO.getPassword(), user.getPassword())) {
            return false; // 현재 비밀번호가 일치하지 않으면 false 반환
        }

        // 새로운 비밀번호가 입력된 경우 변경
        if (memberDTO.getNewPassword() != null && !memberDTO.getNewPassword().isEmpty()) {
            memberDTO.setPassword(passwordEncoder.encode(memberDTO.getNewPassword())); // 새 비밀번호 암호화
        } else {
            // 새로운 비밀번호가 없으면 기존 비밀번호 유지
            memberDTO.setPassword(user.getPassword());
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
        if (memberDTO.getAddr() == null || memberDTO.getAddr().isEmpty()) {
            memberDTO.setAddr(user.getAddr());
        }
        if (memberDTO.getPostNum() == null || memberDTO.getPostNum().isEmpty()) {
            memberDTO.setPostNum(user.getPostNum());
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
}
