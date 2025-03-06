package com.care.project.main.service;

import java.util.List;
import java.util.Map;


public interface ScheduleService {
	public List<Map<String, Object>> scheduleDate(String title);
	public List<Map<String, Object>> scheduleInfo(String startdate,String title);
	public Map<String, Object> scheduleDetailData(Integer scheduleid);
	public List<Map<String, Object>> reservedSeats(Integer scheduleid);
	
}
