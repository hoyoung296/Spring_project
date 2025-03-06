package com.care.project.main.service;

import java.util.List;

public interface ReserveService {
	public Long createReservation(String userId, Integer scheduleid, Integer totalAmount);
	public List<Integer> seatStatus(Integer scheduleid, List<String> seatIds);
	public boolean reserveSeats(Long reservationId,List<Integer> seatStatusIds);
	public boolean updateSeatStatusType(List<Integer> seatStatusIds);
	public boolean updateSeatStatusType2(List<Integer> seatStatusIds);
	public boolean cancelReservation(Long reservationId,Integer scheduleid, List<Integer> seatStatusIds);
	//예매아디로 스케줄 아이디 구하기
	public int getSchedulId(Long reservationId);
}
