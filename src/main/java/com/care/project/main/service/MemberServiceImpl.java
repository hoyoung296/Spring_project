package com.care.project.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.care.project.main.dto.MemberDTO;
import com.care.project.main.mapper.MemberMapper;

@Service
public class MemberServiceImpl implements MemberService{
	@Autowired
	private MemberMapper memberMapper;

    @Override
    public void registerMember(MemberDTO memberDTO) {
        memberMapper.register(memberDTO);
    }

    @Override
    public MemberDTO loginMember(MemberDTO memberDTO) {
        return memberMapper.login(memberDTO);
    }

    @Override
    public void updateMember(MemberDTO memberDTO) {
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