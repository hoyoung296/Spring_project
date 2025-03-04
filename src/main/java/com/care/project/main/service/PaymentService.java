package com.care.project.main.service;

import com.care.project.main.dto.PaymentDTO;

public interface PaymentService {
    // 결제 정보 저장 (pending 상태)
    public Integer createPayment(PaymentDTO payment);
    
    // 포트원 결제 ID(portonePaymentId, imp_uid) 기반 결제 검증
    public boolean verifyPayment(String portonePaymentId, int expectedAmount);
}