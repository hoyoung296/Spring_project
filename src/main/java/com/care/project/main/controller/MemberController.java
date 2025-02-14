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
        if (ms.isUserIdDuplicate(memberDTO.getUserId())) {
            return "등록된 아이디입니다.";
        }
        if (ms.isEmailDuplicate(memberDTO.getEmail())) {
            return "등록된 이메일입니다.";
        }
        ms.registerMember(memberDTO);
        return "회원가입이 완료되었습니다.";
    }
    // 아이디 중복 체크
    @GetMapping("/check-id")
    public boolean checkUserId(@RequestParam String userId) {
        return ms.isUserIdDuplicate(userId);
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return ms.isEmailDuplicate(email);
    }
    
    // 비밀번호 확인
    @PostMapping(value = "/check-password", produces = "text/plain;charset=UTF-8")
    public String checkPassword(@RequestBody MemberDTO memberDTO) {
        return ms.checkPassword(memberDTO) ? "비밀번호 확인 성공" : "비밀번호가 일치하지 않습니다.";
    }

    // 로그인
    @PostMapping(value = "/login", produces = "text/plain;charset=UTF-8")
    public String login(@RequestBody MemberDTO memberDTO) {
        return ms.loginMember(memberDTO) ? "로그인 성공" : "아이디 또는 비밀번호가 일치하지 않습니다.";
    }
    
    // 회원정보 수정 (비밀번호 확인 추가)
    @PutMapping(value = "/update", produces = "text/plain;charset=UTF-8")
    public String updateMember(@RequestBody MemberDTO memberDTO) {
        if (!ms.checkPassword(memberDTO)) {
            return "비밀번호가 일치하지 않습니다.";
        }
        return ms.updateMember(memberDTO) ? "회원정보가 수정되었습니다." : "회원정보 수정 실패";
    }

    // 회원 탈퇴 (비밀번호 확인 추가)
    @DeleteMapping(value = "/delete", produces = "text/plain;charset=UTF-8")
    public String deleteMember(@RequestBody MemberDTO memberDTO) {
        if (!ms.checkPassword(memberDTO)) {
            return "비밀번호가 일치하지 않습니다.";
        }
        return ms.deleteMember(memberDTO.getUserId()) ? "회원탈퇴가 완료되었습니다." : "회원탈퇴 실패";
    }

    // 사용자 정보 조회
    @GetMapping(value = "/info", produces = "application/json;charset=UTF-8")
    public MemberDTO getMemberInfo(@RequestParam String userId) {
        return ms.getMember(userId);
    }
}
