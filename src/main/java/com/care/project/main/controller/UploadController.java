package com.care.project.main.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class UploadController {

	private static final String IMAGE_DIR = "C:/Users/USER/Desktop/job/spring-workspace/movieProjectBack/src/main/webapp/resources/images/";

	@PostMapping
	public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			// 폴더 없으면 생성
			File directory = new File(IMAGE_DIR);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			// 파일 저장 경로 설정
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
			System.out.println("파일이름 확인 : " + file.getOriginalFilename());
			Path filePath = Paths.get(IMAGE_DIR, fileName);
			System.out.println("✅ 최종 저장 경로: " + filePath);

			// 파일 저장
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("✅ 파일 저장 완료!");

			Map<String, String> response = new HashMap<>();
			response.put("imagename", fileName);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/image")
	public ResponseEntity<?> returnImage(@RequestParam("image") String imageName) {
		try {
			System.out.println("이미지 요청: " + imageName);

			// 이미지 파일 경로 설정
			Path imagePath = Paths.get(IMAGE_DIR + imageName);
			Resource resource = new UrlResource(imagePath.toUri());

			// 파일 존재 여부 확인
			if (!resource.exists() || !resource.isReadable()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이미지를 찾을 수 없습니다.");
			}

			// Content-Type 설정 (JPG, PNG 등 자동 감지)
			String contentType = "image/jpeg"; // 기본값 (JPG)
			if (imageName.toLowerCase().endsWith(".png")) {
				contentType = "image/png";
			} else if (imageName.toLowerCase().endsWith(".gif")) {
				contentType = "image/gif";
			}

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(resource);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 로딩 중 오류 발생");
		}
	}
}