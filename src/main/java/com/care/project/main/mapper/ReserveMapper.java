package com.care.project.main.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReserveMapper {
	public Long insertReservation(
	        @Param("reservationId") Long reservationId,
	        @Param("userId") String userId,
	        @Param("scheduleid") int scheduleId,
	        @Param("reservationStatusId") int reservationStatusId,
	        @Param("totalAmount") int totalAmount
	    );
	public Integer getSeatStatusId(
			@Param("scheduleid") int scheduleid, 
			@Param("seatId") String seatId);

	public Integer insertReservationSeat(
			@Param("reservationId") long reservationId,
			@Param("seatStatusId") int seatStatusId );
	public Integer updateSeatStatusType(
			@Param("seatStatusId") int seatStatusId,@Param("seatStatusTypeId") int seatStatusTypeId);
	public Integer deleteReservationSeats(
			@Param("reservationId") long reservationId);
	public Integer updateReservation(
			@Param("reservationId") long reservationId,@Param("status") int status);
	
	//스케줄아이디 구하기
	public Integer getSchedulId(@Param("reservationId") long reservationId);
	
	// 예매된 좌석번호 구하기
	public List<Integer> getReserveSeatStatusId(@Param("reservationId") long reservationId);
	
}
