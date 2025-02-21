package com.care.project.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.care.project.main.dto.MovieDTO;

@Mapper
public interface AdminMapper {
   // 모든 영화 데이터 삽입
   void insertMovie(@Param("movie") MovieDTO movie);
   
   // 기존 영화 찾기
    MovieDTO findByMovieId(@Param("movieId") int movieId);

    // 기존 영화 업데이트
    void updateMovie(@Param("movie") MovieDTO movie);
}