package com.care.project.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.main.dto.MovieDTO;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	AdminService adminService;

	@GetMapping("/movie/popular")
	public List<MovieDTO> getPopularMovies() {
		return adminService.getPopularBoxOfficeMovies();
	}
}