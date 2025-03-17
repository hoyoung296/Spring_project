package com.care.project.main.service;

import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.care.project.main.dto.PaymentDTO;
import com.care.project.main.mapper.PaymentMapper;
import com.care.project.main.mapper.ReserveMapper;

@Slf4j
@PropertySource("classpath:application.properties")
@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentMapper paymentMapper;
	@Autowired
	private ReserveService reserver;
	@Autowired
	private ReserveMapper reserveMapper;

	@Value("${portone.store.id}")
	private String storeId;

	@Value("${portone.api.secret}")
	private String apiSecret;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	@Transactional
	public Integer createPayment(PaymentDTO payment) {
		payment.setPaymentStatus("pending");
		System.out.println("##### Payment: " + payment);
		return paymentMapper.insertPayment(payment);
	}

	/**
	 * PortOne Access Token 발급 (V2)
	 */
	public String getAccessToken() {
		// V2 API 토큰 발급 엔드포인트
		String url = "https://api.portone.io/login/api-secret";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Body에 apiSecret을 JSON 형태로 담아 전송
		Map<String, String> body = new HashMap<>();
		body.put("apiSecret", apiSecret);

		HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

			// 디버깅용 로그
			System.out.println("🔍 PortOne API 응답 코드: " + response.getStatusCode());
			System.out.println("🔍 PortOne API 응답 바디: " + response.getBody());

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				JsonNode responseBody = objectMapper.readTree(response.getBody());
				// V2에서 accessToken 키를 바로 꺼낼 수 있음
				return responseBody.get("accessToken").asText();
			}
		} catch (Exception e) {
			System.err.println("❌ PortOne Access Token 발급 실패: " + e.getMessage());
		}
		return null;
	}

	/**
	 * 결제 검증 (paymentId 기반, V2 API)
	 */
	@Override
	public boolean verifyPayment(String portonePaymentId, int expectedAmount, int scheduleId,
			List<Integer> seatStatusIds) {
		String token = getAccessToken();
		if (token == null)
			return false;

		String url = "https://api.portone.io/payments/" + portonePaymentId
				+ "?storeId=store-e78aba4d-3df0-4c76-896c-ba009858ddcd";

		System.out.println("@portonePaymentId : " + portonePaymentId);
		System.out.println("@expectedAmount : " + expectedAmount);
		System.out.println("@token : " + token);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				// 응답 전체 JSON 객체를 파싱 및 로그 출력
				JsonNode responseBody = objectMapper.readTree(response.getBody());
				System.out.println("Response JSON: " + responseBody.toString());

				// amount 객체에서 total 필드를 가져옴
				JsonNode amountNode = responseBody.get("amount");
				if (amountNode == null) {
					System.err.println("❌ 응답 JSON에 'amount' 필드가 없습니다.");
					return false;
				}
				int actualAmount = amountNode.get("total").asInt();

				// status 필드는 그대로 가져옴
				JsonNode statusNode = responseBody.get("status");
				if (statusNode == null) {
					System.err.println("❌ 응답 JSON에 'status' 필드가 없습니다.");
					return false;
				}
				String status = statusNode.asText();

				// 결제 성공(PAID) && 금액 일치 여부 확인 (대소문자 무시)
				if ("PAID".equalsIgnoreCase(status) && actualAmount == expectedAmount) {
					int result = paymentMapper.updatePaymentStatusByPortoneId(portonePaymentId, status);
					int rs = reserveMapper.updateReservation(Long.parseLong(portonePaymentId), 2);
					System.out.println("result : " + result);
					System.out.println("rs : " + rs);
					return true;
				} else {
					boolean result = cancelPayment(portonePaymentId, "결제 검증 실패");
//                	int result = paymentMapper.deletePayment(Long.parseLong(portonePaymentId));
//                	int result = paymentMapper.updatePaymentStatusByPortoneId(portonePaymentId, "cancel");
					System.out.println("del result : " + result);
					if (result) {
						boolean isDeleted = reserver.cancelReservation(Long.parseLong(portonePaymentId), scheduleId,
								seatStatusIds);
						System.out.println("isDeleted : " + isDeleted);
					}
					return false;
				}
			}
		} catch (Exception e) {
			boolean result = cancelPayment(portonePaymentId, "결제 검증 실패");
//        	int result = paymentMapper.deletePayment(Long.parseLong(portonePaymentId));
//        	int result = paymentMapper.updatePaymentStatusByPortoneId(portonePaymentId, "cancel");
			System.out.println("del result : " + result);
			if (result) {
				boolean isDeleted = reserver.cancelReservation(Long.parseLong(portonePaymentId), scheduleId,
						seatStatusIds);
				System.out.println("isDeleted : " + isDeleted);
			}
			System.err.println("❌ PortOne 결제 검증 실패: " + e.getMessage());
		}
		return false;
	}

	@Override
	public boolean cancelPayment(String paymentId, String reason) {
		// 토큰 발급
		String token = getAccessToken();
		if (token == null)
			return false;

		// paymentId는 경로 변수로 포함, 취소 API 엔드포인트 호출
		String url = "https://api.portone.io/payments/" + paymentId + "/cancel";

		System.out.println("@paymentId : " + paymentId);
		System.out.println("@reason : " + reason);
		System.out.println("@token : " + token);

		// HTTP 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);

		// 필수 항목인 취소 사유(reason)만 요청 본문에 포함
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("reason", reason);
		requestBody.put("storeId", storeId);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				System.out.println("Cancel Payment Response: " + response.getBody());
				paymentMapper.updatePaymentStatusByPortoneId(paymentId, "cancel");
				return true;
			} else {
				System.err.println("❌ Cancel payment failed. Status code: " + response.getStatusCode());
			}
		} catch (Exception e) {
			System.err.println("❌ Cancel payment exception: " + e.getMessage());
		}
		return false;
	}
}