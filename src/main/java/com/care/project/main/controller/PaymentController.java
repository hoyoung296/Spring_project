package com.care.project.main.controller;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.common.CommonResponse;
import com.care.project.common.Constant;
import com.care.project.common.ErrorType;
import com.care.project.main.dto.PaymentDTO;
import com.care.project.main.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("member/payment")
public class PaymentController {
	
	private PaymentService paymentService;
	
	// 결제 정보 저장 (pending 상태)
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO payment) {
        paymentService.createPayment(payment);
        return ResponseEntity.ok("{\"message\": \"결제 정보가 성공적으로 저장되었습니다.\"}");
    }
}
