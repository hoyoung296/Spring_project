package com.care.project.main.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.main.dto.MovieDTO;
import com.care.project.main.dto.ReviewDTO;
import com.care.project.main.service.ReviewService;

@RestController
@RequestMapping("review")
public class ReviewController {
	@Autowired
	ReviewService rvs;
	
	@GetMapping("/list")
	public List<MovieDTO> list() {
		return rvs.list();
	}
	
	@GetMapping("/search")
	public List<MovieDTO> getList(@RequestParam String id) {
		return rvs.getList(id);
	}

	@GetMapping("/searchInfo")
	public List<Map<String, Object>> searchInfo(@RequestParam int id) {
		return rvs.searchInfo(id);
	}

	@GetMapping("/info")
	public Map<String, Object> getInfo(@RequestParam String id,
			@RequestParam(required = false, defaultValue = "1") int start) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dto", rvs.getInfo(id, start));
		map.put("page", rvs.getCount(id));
		return map;
	}

	@GetMapping("/reserve")
	public Map<String, Object> getReserve(@RequestParam String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dto", rvs.getReserve(id));
		return map;
	}

	@GetMapping("/reviewCheck")
	public int reviewcheck(@RequestParam String id, @RequestParam int movieid) {
		return rvs.reviewCheck(id, movieid);
	}

	@PostMapping("/writeReview")
	public int writeReview(@RequestBody ReviewDTO dto) {
		int result = rvs.writeReview(dto);
		return result;
	}
}