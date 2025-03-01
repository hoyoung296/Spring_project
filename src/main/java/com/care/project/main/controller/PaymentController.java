package com.care.project.main.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private PaymentService paymentService;

	
	// 결제 정보 저장 (pending 상태)
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO payment) {
    	try {
    		payment.setPaymentId(payment.getReservationId());
//    		System.out.println("값@@@@@ : " + payment.toString());
	    	int result = paymentService.createPayment(payment);
	    	System.out.println("!!!!result controller : " + result);
	    	Map<String, Object> responseData = new HashMap<>();
	    	if(result>0) {
	    		responseData.put("paymentId", payment.getPaymentId().toString());
	    		System.out.println("responseData : "+responseData); 
	    	}
	    	return CommonResponse.createResponse(
 	                CommonResponse.builder()
 	                        .code(Constant.Success.SUCCESS_CODE)
 	                        .message("Success")
 	                        .data(responseData)
 	                        .build(),
 	                HttpStatus.OK
 	        );
    	} catch (Exception e) {
    		log.info("createPayment Error ");
			e.printStackTrace();

			return CommonResponse.createResponse(CommonResponse.builder().code(ErrorType.ETC_FAIL.getErrorCode())
					.message(ErrorType.ETC_FAIL.getErrorMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> request) {
        String portonePaymentId = request.get("portonePaymentId");
        System.out.println("@@portonePaymentId" + portonePaymentId);
        boolean isValid = paymentService.verifyPayment(portonePaymentId, 0); // 금액 검증 포함
        
        Map<String, Object> responseData = new HashMap<>();
        
        if (isValid) {
        	responseData.put("rs", "성공");
            return CommonResponse.createResponse(
 	                CommonResponse.builder()
                     .code(Constant.Success.SUCCESS_CODE)
                     .message("Success")
                     .data(responseData)
                     .build(),
             HttpStatus.OK
            		 );
        } else {
        	responseData.put("rs", "실패");
        	return CommonResponse.createResponse(CommonResponse.builder().code(ErrorType.ETC_FAIL.getErrorCode())
					.message(ErrorType.ETC_FAIL.getErrorMessage())
					.data(responseData).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
