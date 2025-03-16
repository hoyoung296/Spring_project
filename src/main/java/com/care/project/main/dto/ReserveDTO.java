package com.care.project.main.dto;

public class ReserveDTO {
	private long reservationId;
	private int scheduleId, reservationStatusId, totalAmount;
	// reservation_ID => reservationId, schedule_ID => scheduleId,
	// reservation_Status_ID => reservationStatusId,
	// total_Amount => totalAmount
	private String userId; // user_ID => userId

	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public int getReservationStatusId() {
		return reservationStatusId;
	}

	public void setReservationStatusId(int reservationStatusId) {
		this.reservationStatusId = reservationStatusId;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}