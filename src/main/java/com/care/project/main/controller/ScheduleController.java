package com.care.project.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.main.service.ReserveService;

@RestController
@RequestMapping("member")
public class ScheduleController {

@Autowired ReserveService rs;
	
	@GetMapping("/schedul")
	public String getResrve() {
		return null;
	}
	
}
