package com.care.project.admin;

import java.util.List;

import com.care.project.main.dto.MemberDTO;
import com.care.project.main.dto.MovieDTO;
import com.care.project.main.dto.ScheduleDTO;
import com.care.project.main.dto.ScreenDTO;

public interface AdminService {
	//기존 메서드
	public List<MovieDTO> getPopularBoxOfficeMovies(); 
	public void scheduledFetchAndUpdateMovies();
	
	// 추가한 메서드
    MovieDTO getMovieById(int movieId);  // 기존 영화 정보 가져오기
    int editMovie(MovieDTO movie);    // 영화 정보 업데이트
    
    //회원 리스트 가져오기
    List<MemberDTO> getUserList();
	
    List<ScreenDTO> getAllScreens();  // 모든 상영관 조회
    List<ScheduleDTO> getAllSchedules();  // 모든 상영 일정 조회
    
    int insertSchedule(ScheduleDTO scheduleDTO); //상영일정 업데이트
    
    int deleteSchedule(int scheduleId); //상영일정 삭제
}