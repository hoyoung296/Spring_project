package com.care.project.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.main.dto.MemberDTO;
import com.care.project.main.dto.MovieDTO;
import com.care.project.main.dto.ScheduleDTO;
import com.care.project.main.dto.ScreenDTO;

@RestController
@RequestMapping("admin")
public class AdminController {
	@Autowired
	AdminService AdminService;

	@PostMapping("/movie/popular")
	public void getList() {
		AdminService.getPopularBoxOfficeMovies();
	}

	// 수정된 영화 정보 업데이트
	@PutMapping("/edit_movie")
	public ResponseEntity<Map<String, Object>> updateMovie(@RequestBody MovieDTO movie) {
		int result = AdminService.editMovie(movie); // 영화 정보 업데이트
		Map<String, Object> map = new HashMap<String, Object>();
		if (result == 1) {
			map.put("message", "수정 성공");
			return ResponseEntity.ok(map); // HTTP 200
		} else if (result == -1) { // 예를 들어 없는 ID
			map.put("message", "해당 영화를 찾을 수 없음");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map); // HTTP 404
		} else {
			map.put("message", "수정 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map); // HTTP 500
		}
	}

	@GetMapping("/members")
	public List<MemberDTO> getUserList() {
		return AdminService.getUserList();
	}

	@GetMapping("/screens")
	public List<ScreenDTO> getAllScreens() {
		return AdminService.getAllScreens();
	}

	@GetMapping("/schedules")
	public List<Map<String, Object>> getAllSchedules() {
		return AdminService.getAllSchedules();
	}

	@PostMapping("/schedule/insert")
	public ResponseEntity<Map<String, Object>> insertSchedule(@RequestBody List<ScheduleDTO> scheduleList) {
		int result = AdminService.insertSchedule(scheduleList);
		Map<String, Object> map = new HashMap<String, Object>();
		if (result > 0) {
			map.put("message", "상영 일정 입력 완료");
			return ResponseEntity.ok(map); // HTTP 200
		} else if (result == -1) { // 예를 들어 없는 ID
			map.put("message", "해당 일정을 찾을 수 없음");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map); // HTTP 404
		} else {
			map.put("message", "상영 일정 입력 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map); // HTTP 500
		}
	}

	@DeleteMapping("/schedule/delete")
	public ResponseEntity<Map<String, Object>> deleteSchedule(@RequestParam int id) {
		int result = AdminService.deleteSchedule(id);
		Map<String, Object> map = new HashMap<String, Object>();
		if (result == 1) {
			map.put("message", "상영 일정 삭제 성공");
			return ResponseEntity.ok(map); // HTTP 200
		} else if (result == -1) { // 예를 들어 없는 ID
			map.put("message", "해당 일정을 찾을 수 없음");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map); // HTTP 404
		} else {
			map.put("message", "상영 일정 삭제 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map); // HTTP 500
		}
	}

	@PostMapping("/movie/insert")
	public ResponseEntity<Map<String, Object>> insertMovie(@RequestBody MovieDTO dto) {
		int result = AdminService.insertMovie(dto);
		Map<String, Object> map = new HashMap<String, Object>();
		if (result == 1) {
			map.put("message", "영화 추가 성공");
			return ResponseEntity.ok(map); // HTTP 200
		} else if (result == -1) { // 예를 들어 없는 ID
			map.put("message", "해당 영화를 찾을 수 없음");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map); // HTTP 404
		} else {
			map.put("message", "영화 추가 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map); // HTTP 500
		}
	}
}