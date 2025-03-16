package com.care.project.main.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.care.project.main.dto.ScheduleDTO;
import com.care.project.main.dto.ScreenDTO;

@Mapper
public interface ScheduleMapper {
	public List<Map<String, Object>> getScheduleDate(String title);

	public List<Map<String, Object>> getScheduleInfo(@Param("startdate") String startdate,
			@Param("title") String title);

	public Map<String, Object> scheduleDetailData(Integer scheduleid);

	public List<Map<String, Object>> reservedSeats(Integer scheduleid);

	public List<ScreenDTO> getAllScreens(); // 모든 상영관 조회

	public List<Map<String, Object>> getAllSchedules(); // 모든 상영 일정 조회

	public int insertSchedule(ScheduleDTO schedule); // 상영 일정 추가

	public int deleteSchedule(int scheduleId); // 상영 일정 삭제
}