package com.care.project.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.care.project.main.dto.MovieDTO;

@Mapper
public interface AdminMapper {
    void resetSequence();
    void insertMovie(@Param("movie") MovieDTO movie);
    MovieDTO checkExistingMovie(@Param("movieId") int movieId);
    void updateMovie(@Param("movie") MovieDTO movie);

    // 추가: 모든 movie_id 가져오는 쿼리
    List<Integer> getAllMovieIds();
}