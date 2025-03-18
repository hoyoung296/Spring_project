package com.care.project.main.dto;

import java.util.List;

import lombok.Data;

@Data
public class PaymentCancelRequest {
	private String portonePaymentId;
	private int amount;
	private int scheduleId;
	private List<String> seatIds;
}