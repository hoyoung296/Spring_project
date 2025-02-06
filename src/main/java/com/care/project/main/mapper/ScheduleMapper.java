package com.care.project.main.mapper;

import java.util.Map;

import com.care.project.main.dto.ScheduleDTO;

public interface ScheduleMapper {
	public Map<String, Object> getSchedule(String title);
}
