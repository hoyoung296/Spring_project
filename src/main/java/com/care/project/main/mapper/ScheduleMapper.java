package com.care.project.main.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ScheduleMapper {
	public List<Map<String, Object>> getScheduleDate(String title);
	public List<Map<String, Object>> getScheduleInfo(String startdate);
	public Map<String, Object> scheduleDetailData(Integer scheduleid);
	public List<Map<String, Object>> reservedSeats(Integer scheduleid);
	public Long insertReservation(
	        @Param("reservationId") Long reservationId,
	        @Param("userId") String userId,
	        @Param("scheduleid") int scheduleId,
	        @Param("reservationStatusId") int reservationStatusId,
	        @Param("totalAmount") int totalAmount
	    );
}
