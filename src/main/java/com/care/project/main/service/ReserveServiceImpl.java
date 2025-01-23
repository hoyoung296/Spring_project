package com.care.project.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.care.project.main.mapper.ReserveMapper;

@Service
public class ReserveServiceImpl implements ReserveService {
	@Autowired ReserveMapper res;

}