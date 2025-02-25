package com.care.project.admin;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.care.project.main.dto.MemberDTO;
import com.care.project.main.dto.MovieDTO;

@RestController
@RequestMapping("admin")
public class AdminController {
	@Autowired
	AdminService AdminService;
	
	@PostMapping("/movie/popular")
	public void getList() {
		AdminService.getPopularBoxOfficeMovies();
	}
	
	//수정된 영화 정보 업데이트
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
    
 // 영화 이미지 저장
    @PostMapping("/upload_movie_image")
    public ResponseEntity<Map<String, Object>> uploadMovieImage(
            @RequestParam("basePath") String basePath, // 앞쪽 경로를 받아옴
            @RequestParam("movieId") String movieId,
            @RequestParam("image") MultipartFile file,
            @RequestParam("field") String field) {

        Map<String, Object> response = new HashMap<>();

        // 파일이 비었는지 확인
        if (file.isEmpty()) {
            response.put("message", "파일이 비어있음");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // 저장 경로 설정 (팀원별 앞쪽 경로를 basePath로 받음)
            String uploadDir = Paths.get(basePath, "movieProjectFront", "public", "img", "poster").toString();

            // 파일명 유지
            String originalFilename = file.getOriginalFilename();
            String filePath = Paths.get(uploadDir, originalFilename).toString();
            File destFile = new File(filePath);

            // 파일 저장
            file.transferTo(destFile);

            // 저장된 이미지의 URL 생성
            String imageUrl = "/img/poster/" + originalFilename; // 프론트에서 접근할 수 있도록 경로 설정

            // 프론트엔드로 응답 반환
            response.put("message", "이미지 업로드 성공");
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "이미지 저장 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }	
    
    
    @GetMapping("/members")
    public List<MemberDTO> getUserList() {
        return AdminService.getUserList();  // 서비스 호출 및 반환
    }
}
    
    
    
   