package com.care.project.main.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.care.project.main.mapper.ScheduleMapper;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired 
    private ScheduleMapper scheduleMapper;

    @Override
    public Map<String, Object> scheduleDetails(String title) {
    	System.out.println("데이터 : " + scheduleMapper.getSchedule(title));
        Map<String, Object> scheduleData = scheduleMapper.getSchedule(title);
        System.out.println("데이터값: " + scheduleData);
        return scheduleData;
    }
}
