package com.care.project.main.controller;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.common.CommonResponse;
import com.care.project.common.Constant;
import com.care.project.common.ErrorType;
import com.care.project.main.dto.PaymentDTO;
import com.care.project.main.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class PaymentController {
	
	private PaymentService paymentService;
	
	@PostMapping("/payment/create")
	public ResponseEntity<?> createPayment(@RequestParam Long reservationId,
            @RequestParam Long paymentMethodId,
            @RequestParam Double amount,
            @RequestParam String portonePaymentId,
            @RequestParam(required = false) String receiptUrl) {
		try {
			PaymentDTO payment = new PaymentDTO();
			payment.setReservationId(reservationId);
			payment.setPaymentMethodId(paymentMethodId);
			payment.setAmount(amount);
			payment.setPaymentStatus("pending");
			payment.setPortonePaymentId(portonePaymentId);
			payment.setReceiptUrl(receiptUrl);
			
			// 결제 생성 처리 (paymentId가 세팅된다고 가정)
			paymentService.createPayment(payment);
			
			return CommonResponse.createResponse(
					CommonResponse.builder()
					.code(Constant.Success.SUCCESS_CODE)  // 또는 직접 숫자(예:200)를 사용할 수 있습니다.
					.message("Payment created successfully")
					.data(Collections.singletonMap("paymentId", payment.getPaymentId()))
					.build(), HttpStatus.OK);
		} catch (Exception e) {
			log.info("createPayment Error ");
			e.printStackTrace();

			return CommonResponse.createResponse(CommonResponse.builder().code(ErrorType.ETC_FAIL.getErrorCode())
					.message(ErrorType.ETC_FAIL.getErrorMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
