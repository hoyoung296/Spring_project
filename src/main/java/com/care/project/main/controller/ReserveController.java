package com.care.project.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.main.service.ReserveService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ReserveController {
	@Autowired ReserveService rs;
	
}