package com.care.project.admin;

import java.util.List;
import java.util.Map;

import com.care.project.main.dto.MemberDTO;
import com.care.project.main.dto.MovieDTO;
import com.care.project.main.dto.ScheduleDTO;
import com.care.project.main.dto.ScreenDTO;

public interface AdminService {
	public List<MovieDTO> getPopularBoxOfficeMovies(); 
	public void scheduledFetchAndUpdateMovies();
    public MovieDTO getMovieById(int movieId);  // 기존 영화 정보 가져오기
    public int editMovie(MovieDTO movie);    // 영화 정보 업데이트
    public List<MemberDTO> getUserList(); //회원 리스트 가져오기
    public List<ScreenDTO> getAllScreens();  // 모든 상영관 조회
    public List<Map<String, Object>> getAllSchedules();  // 모든 상영 일정 조회
    public int insertSchedule(List<ScheduleDTO> scheduleList); //상영일정 업데이트
    public int deleteSchedule(int scheduleId); //상영일정 삭제
    public int insertMovie(MovieDTO dto);
    public int deleteMovie(int movieId);
}