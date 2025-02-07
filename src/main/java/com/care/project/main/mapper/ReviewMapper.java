package com.care.project.main.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.care.project.main.dto.MovieDTO;
import com.care.project.main.dto.ReviewDTO;

public interface ReviewMapper {
	public List<MovieDTO> getList(@Param("id") String id);
	public List<Map<String, Object>> searchInfo(int id);
	public List<Map<String, Object>> getInfo(@Param("id") String id, @Param("start") int start);
	public int getCount(String id);
	public List<Map<String, Object>> getReserve(@Param("id") String id,@Param("start") int start);
	public int getReserveCount(String id);
	public MovieDTO reserveInfo (int id);
	public int writeReview(ReviewDTO dto);
	public int delReserve(int id);
}