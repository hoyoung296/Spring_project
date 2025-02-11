package com.care.project.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.care.project.main.dto.MemberDTO;
import com.care.project.main.service.MemberService;

@CrossOrigin(origins = "*")
@RestController
public class MemberController {
    @Autowired
    private MemberService ms;

    // 회원가입
    @PostMapping(value = "/register", produces = "text/plain;charset=UTF-8")
    public String register(@RequestBody MemberDTO memberDTO) {
        try {
            // 아이디 중복 체크
            if (ms.getMemberInfo(memberDTO.getUserId()) != null) {
                return "아이디가 이미 존재합니다.";
            }
            // 이메일 중복 체크
            if (ms.getMemberInfo(memberDTO.getEmail()) != null) {
                return "이메일이 이미 존재합니다.";
            }

            ms.registerMember(memberDTO);
            return "회원가입 성공";
        } catch (Exception e) {
            e.printStackTrace();
            return "회원가입 실패: " + e.getMessage();
        }
    }

    // 로그인
    @PostMapping(value = "/login", produces = "text/plain;charset=UTF-8")
    public String login(@RequestBody MemberDTO memberDTO) {
        MemberDTO loginUser = ms.loginMember(memberDTO);
        if (loginUser != null) {
            return "로그인 성공";
        } else {
            return "아이디 또는 비밀번호가 일치하지 않습니다.";
        }
    }

    // 회원 정보 수정
    @PutMapping(value = "/update", produces = "text/plain;charset=UTF-8")
    public String updateMember(@RequestBody MemberDTO memberDTO) {
        try {
            ms.updateMember(memberDTO);
            return "회원정보 수정 성공";
        } catch (Exception e) {
            return "회원정보 수정 실패: " + e.getMessage();
        }
    }

    // 회원 탈퇴
    @DeleteMapping(value = "/delete", produces = "text/plain;charset=UTF-8")
    public String deleteMember(@RequestParam String userId) {
        try {
            ms.deleteMember(userId);
            return "회원탈퇴 성공";
        } catch (Exception e) {
            return "회원탈퇴 실패: " + e.getMessage();
        }
    }

    // 사용자 정보 조회
    @GetMapping(value = "/info", produces = "application/json;charset=UTF-8")
    public MemberDTO getMemberInfo(@RequestParam String userId) {
        return ms.getMemberInfo(userId);
    }
}
