package com.care.project.admin;

import java.util.List;

import com.care.project.main.dto.MovieDTO;

public interface AdminService {
	//기존 메서드
	public List<MovieDTO> getPopularBoxOfficeMovies(); 
	public void scheduledFetchAndUpdateMovies();
	
	// 추가한 메서드
    MovieDTO getMovieById(int movieId);  // 기존 영화 정보 가져오기
    int editMovie(MovieDTO movie);    // 영화 정보 업데이트
	
}