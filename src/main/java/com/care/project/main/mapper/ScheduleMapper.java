package com.care.project.main.mapper;

import java.util.List;
import java.util.Map;

import com.care.project.main.dto.ScheduleDTO;

public interface ScheduleMapper {
	public Map<String, Object> getScheduleDate(String title);
	public Map<String, Object> getScheduleInfo(String startdate);
	public Map<String, Object> scheduleDetailData(Integer scheduleid);
	public List<Map<String, Object>> reservedSeats(Integer scheduleid);
	
}
