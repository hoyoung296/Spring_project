package com.care.project.main.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.main.dto.ReviewDTO;
import com.care.project.main.dto.ReviewSearchDTO;
import com.care.project.main.service.ReviewService;

@RestController
@RequestMapping("/review")
public class ReviewController {
	@Autowired
	ReviewService rvs;

	@GetMapping("/search")
	public List<ReviewSearchDTO> getList() {
		return rvs.getList(null);
	}

	@GetMapping("/search/{id}")
	public List<ReviewSearchDTO> getList(@PathVariable String id) {
		return rvs.getList(id);
	}
	
	@GetMapping("/searchinfo/{id}")
	public List<ReviewDTO> searchInfo(@PathVariable int id){
		return rvs.searchInfo(id);
	}
	
	@GetMapping("/info")
	public Map<String , Object> getinfo(@RequestParam String id,@RequestParam(required = false, defaultValue= "1") int start){
		Map<String, Object> map = new HashMap<String, Object>();
		rvs.getInfo(id,start);
		rvs.getCount(id);
		map.put("dto", rvs.getInfo(id,start));
		map.put("page", rvs.getCount(id));
		return map;
	}
}