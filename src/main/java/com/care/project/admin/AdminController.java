package com.care.project.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdminController {
	@Autowired
	AdminService AdminService;
	
	@PostMapping("/movie/popular")
	public void getList() {
		AdminService.getPopularBoxOfficeMovies();
	}
}