package com.care.project.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.care.project.main.dto.MovieDTO;

@Mapper
public interface AdminMapper {
	void insertMovie(@Param("movie") MovieDTO movie);
}