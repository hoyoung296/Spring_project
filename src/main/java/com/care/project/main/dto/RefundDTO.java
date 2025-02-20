package com.care.project.main.dto;

import lombok.Data;

@Data
public class RefundDTO {
	private Long refundId;
    private Long paymentId;
    private Double refundAmount;
    private String refundStatus;   // pending, refunded 등
    private String refundTimestamp;
}
