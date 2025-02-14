package com.care.project.main.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.care.project.main.mapper.ScheduleMapper;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired 
    private ScheduleMapper scheduleMapper;

    @Override
    public List<Map<String, Object>> scheduleDate(String title) {
//        Map<String, Object> scheduleData = scheduleMapper.getSchedule(title);
//        return scheduleData;
        return scheduleMapper.getScheduleDate(title);
    }

	@Override
	public List<Map<String, Object>> scheduleInfo(String startdate) {
		
		return scheduleMapper.getScheduleInfo(startdate);
	}
	
	@Override
	public Map<String, Object> scheduleDetailData(Integer scheduleid) {
		System.out.println("예매할 영화 : "+ scheduleMapper.scheduleDetailData(scheduleid));
		return scheduleMapper.scheduleDetailData(scheduleid);
	}

	@Override
	public List<Map<String, Object>> reservedSeats(Integer scheduleid) {
//		{
//			  "code": "SUCCESS",
//			  "message": "Success",
//			  "data": {
//			    "movieInfo": {
//			      "title": "더 폴: 디렉터스 컷",
//			      "screenname": "상영관 B",
//			      "startdate": "2025-01-20",
//			      "starttime": "12:45",
//			      "director": "타셈 싱",
//			      "actors": ["리 페이스", "카틴카 언타루"]
//			    },
//			    "reservedSeats": [
//			      { "row": "F", "col": 7 },
//			      { "row": "F", "col": 8 },
//			      { "row": "F", "col": 9 }
//			    ]
//			  }
//			}
		System.out.println("예약된 좌석 : "+ scheduleMapper.reservedSeats(scheduleid));
		return scheduleMapper.reservedSeats(scheduleid);
	}
    
    
}
