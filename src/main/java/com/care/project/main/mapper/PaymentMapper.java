package com.care.project.main.mapper;

import com.care.project.main.dto.PaymentDTO;

public interface PaymentMapper {
	
	// Payment 레코드 삽입 (pending 상태)
    void insertPayment(PaymentDTO payment);
    
    // paymentId를 이용하여 Payment 레코드 조회
    PaymentDTO selectPayment(Long paymentId);
    
    // Payment 레코드의 결제 상태 업데이트
    void updatePaymentStatus(PaymentDTO payment);
}
