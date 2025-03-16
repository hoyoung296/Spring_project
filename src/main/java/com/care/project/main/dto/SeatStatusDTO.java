package com.care.project.main.dto;

public class SeatStatusDTO {
	private String seatId; // seat_ID => seatId
	private int seatStatusId, scheduleId, statusId; // seat_status_ID => seatStatusId, schedule_ID => scheduleId,
													// status_ID => statusId

	public String getSeatId() {
		return seatId;
	}

	public void setSeatId(String seatId) {
		this.seatId = seatId;
	}

	public int getSeatStatusId() {
		return seatStatusId;
	}

	public void setSeatStatusId(int seatStatusId) {
		this.seatStatusId = seatStatusId;
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
}