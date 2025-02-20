package com.care.project.main.dto;

import lombok.Data;

@Data
public class TransactionsDTO {
	private Long transactionId;
    private Long paymentId;
    private String transactionStatus;
    private String transactionMessage;
    private String transactionTimestamp;
}
