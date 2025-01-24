package com.care.project.main.service;

import java.util.List;

import com.care.project.main.dto.ReviewDTO;
import com.care.project.main.dto.ReviewSearchDTO;

public interface ReviewService {
	public List<ReviewSearchDTO> getList(String id);
	public List<ReviewDTO> searchInfo(int id);
}