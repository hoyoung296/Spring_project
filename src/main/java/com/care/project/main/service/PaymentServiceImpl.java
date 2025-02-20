package com.care.project.main.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.care.project.main.dto.PaymentDTO;
import com.care.project.main.mapper.PaymentMapper;

public class PaymentServiceImpl implements PaymentService{

	@Autowired
	private PaymentMapper paymentMapper;
	
	@Override
	public void createPayment(PaymentDTO payment) {
		paymentMapper.insertPayment(payment);
		
	}
	
}
