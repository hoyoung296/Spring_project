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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void registerMember(MemberDTO memberDTO) {
        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);
        memberMapper.register(memberDTO);
    }

    @Override
    public MemberDTO loginMember(MemberDTO memberDTO) {
        MemberDTO loginUser = memberMapper.login(memberDTO);
        if (loginUser != null && passwordEncoder.matches(memberDTO.getPassword(), loginUser.getPassword())) {
            return loginUser;
        }
        return null;
    }

    @Override
    public void updateMember(MemberDTO memberDTO) {
        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);
        memberMapper.updateMember(memberDTO);
    }

    @Override
    public void deleteMember(String userId) {
        memberMapper.deleteMember(userId);
    }

    @Override
    public MemberDTO getMemberInfo(String userId) {
        return memberMapper.getMember(userId);
    }
}
