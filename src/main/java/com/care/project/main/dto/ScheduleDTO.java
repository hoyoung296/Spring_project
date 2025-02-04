package com.care.project.main.dto;

import java.sql.Timestamp;

public class ScheduleDTO {
	private Timestamp start_DT,end_DT;
	private String screen_name, movie_name;
	
	public Timestamp getStart_DT() {
		return start_DT;
	}
	public void setStart_DT(Timestamp start_DT) {
		this.start_DT = start_DT;
	}
	public Timestamp getEnd_DT() {
		return end_DT;
	}
	public void setEnd_DT(Timestamp end_DT) {
		this.end_DT = end_DT;
	}
	public String getScreen_name() {
		return screen_name;
	}
	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}
	public String getMovie_name() {
		return movie_name;
	}
	public void setMovie_name(String movie_name) {
		this.movie_name = movie_name;
	}
	
	
}
