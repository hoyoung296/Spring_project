package com.care.project.main.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.care.project.main.dto.PaymentDTO;

@Mapper
public interface PaymentMapper {
	
	// 결제 정보 저장 (pending 상태)
    Integer insertPayment(PaymentDTO payment);
    
    // paymentId로 Payment 조회
    PaymentDTO selectPayment(@Param("paymentId") Long paymentId);
    
    // portonePaymentId (imp_uid)로 Payment 조회
    PaymentDTO selectPaymentByPortoneId(@Param("portonePaymentId") String portonePaymentId);
    
    // 결제 상태 업데이트 (paymentId 사용)
    Integer updatePaymentStatus(@Param("paymentId") Long paymentId, @Param("paymentStatus") String paymentStatus);

    // portonePaymentId (imp_uid)로 결제 상태 업데이트 추가
    Integer updatePaymentStatusByPortoneId(@Param("portonePaymentId") String portonePaymentId, @Param("paymentStatus") String paymentStatus);
    
    // 결제 삭제 (paymentId를 기준으로 삭제)
    Integer deletePayment(@Param("paymentId") Long paymentId);
}