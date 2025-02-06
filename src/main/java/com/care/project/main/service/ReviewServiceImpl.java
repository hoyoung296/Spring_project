package com.care.project.main.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.care.project.main.dto.ReviewDTO;
import com.care.project.main.dto.ReviewReserveDTO;
import com.care.project.main.dto.ReviewSearchDTO;
import com.care.project.main.mapper.ReviewMapper;

@Service
public class ReviewServiceImpl implements ReviewService{
	@Autowired ReviewMapper rev;
	public List<ReviewSearchDTO> getList(String id){
		List<ReviewSearchDTO> list = null;
		try {
			list = rev.getList(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<ReviewDTO> searchInfo(int id){
		List<ReviewDTO> list = null;
		try {
			list= rev.searchInfo(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<ReviewDTO> getInfo(String id, int start){
		List<ReviewDTO> list = null;
		start=(start-1)*5;
		try {
			list = rev.getInfo(id,start);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public int getCount(String id) {
		int count=0;
		try {
			count = rev.getCount(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int page=count/5;
		if(count%5!=0) {
			page+=1;
		}
		
		return page;
	}
	
	public int writeReview (ReviewDTO dto) {
		int result=0;
		try {
			result=rev.writeReview(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<ReviewReserveDTO> getReserve(String id, int start){
		List<ReviewReserveDTO> list = null;
		start=(start-1)*5;
		try {
			list = rev.getReserve(id,start);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public int getReserveCount(String id) {
		int count=0;
		try {
			count = rev.getReserveCount(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int page=count/5;
		if(count%5!=0) {
			page+=1;
		}
		
		return page;
	}
	
	public int delReserve(int id) {
		int result=0;
		try {
			result=rev.delReserve(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}