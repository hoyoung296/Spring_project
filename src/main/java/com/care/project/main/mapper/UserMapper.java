package com.care.project.main.mapper;

import com.care.project.main.dto.UserDto;

public interface UserMapper {
	UserDto selectUserById(String userId);

	int insertUser(UserDto user);

	int updateUser(UserDto user); // 비밀번호 변경 시 사용할 메서드
}