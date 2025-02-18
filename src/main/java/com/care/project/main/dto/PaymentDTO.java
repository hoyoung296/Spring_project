package com.care.project.main.dto;

import lombok.Data;

@Data
public class PaymentDTO {
	private Long paymentId;
    private Long reservationId;
    private Long paymentMethodId;
    private Double amount;
    private String paymentStatus;  // 예: pending, completed, refunded, failed 등
    private String portonePaymentId; // 포트원에서 받은 결제 ID
    private String receiptUrl;
    private String createdAt;
    private String updatedAt;
}
