package com.care.project.main.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	public Map<String, Object> getReserve(@RequestParam String id,
			@RequestParam(required = false, defaultValue = "1") int start) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dto", rvs.getReserve(id, start));
		map.put("page", rvs.getReserveCount(id));
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

	@DeleteMapping("/del")
	public ResponseEntity<Map<String, Object>> delReserve(@RequestParam String id) {
		long num = Long.parseLong(id);
		Map<String, Object> map = new HashMap<>();
		int result = rvs.delReserve(num);
		if (result == 1) {
			map.put("message", "예매 취소 성공");
			return ResponseEntity.ok(map); // HTTP 200
		} else if (result == -1) { // 예를 들어 없는 ID
			map.put("message", "해당 예매를 찾을 수 없음");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map); // HTTP 404
		} else {
			map.put("message", "예매 취소 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map); // HTTP 500
		}
	}
}