package com.care.project.main.service;

import java.util.List;
import java.util.Map;

import com.care.project.main.dto.MovieDTO;
import com.care.project.main.dto.ReviewDTO;

public interface ReviewService {
	public List<MovieDTO> getList(String id);
	public List<Map<String, Object>> searchInfo(int id);
	public List<Map<String, Object>> getInfo(String id, int start);
	public int getCount (String id);
	public int writeReview (ReviewDTO dto);
	public List<Map<String, Object>> getReserve(String id, int start);
	public int getReserveCount(String id);
	public int delReserve(int id);
 }