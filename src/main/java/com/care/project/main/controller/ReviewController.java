package com.care.project.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.main.service.ReviewService;

@RestController
public class ReviewController {
	@Autowired ReviewService rvs;
	
}