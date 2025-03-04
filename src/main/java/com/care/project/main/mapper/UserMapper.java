package com.care.project.main.mapper;

import com.care.project.main.dto.UserDto;

public interface UserMapper {
    UserDto selectUserById(String userId);
    int insertUser(UserDto user);
}