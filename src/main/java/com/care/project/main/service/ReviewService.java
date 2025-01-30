package com.care.project.main.service;

import java.util.List;

import com.care.project.main.dto.ReviewDTO;
import com.care.project.main.dto.ReviewReserveDTO;
import com.care.project.main.dto.ReviewSearchDTO;

public interface ReviewService {
	public List<ReviewSearchDTO> getList(String id);
	public List<ReviewDTO> searchInfo(int id);
	public List<ReviewDTO> getInfo(String id, int start);
	public int getCount (String id);
	public int writeReview (ReviewDTO dto);
	public List<ReviewReserveDTO> getReserve(String id, int start);
	public int getReserveCount(String id);
	public int delReserve(int id);
 }