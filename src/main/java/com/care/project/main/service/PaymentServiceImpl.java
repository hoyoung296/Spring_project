package com.care.project.main.service;

import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.care.project.main.dto.PaymentDTO;
import com.care.project.main.mapper.PaymentMapper;

@Slf4j
@PropertySource("classpath:application.properties")
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

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
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, request, String.class);

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
    public boolean verifyPayment(String portonePaymentId, int expectedAmount) {
        String token = getAccessToken();
        if (token == null) return false;

        // portonePaymentId가 V2의 paymentId (포트원 거래번호)라고 가정
        String url = "https://api.portone.io/payments/" + portonePaymentId;
        
        System.out.println("@portonePaymentId : "+portonePaymentId);
        System.out.println("@token : "+token);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // V2 API의 응답 구조에 맞춰 파싱
                JsonNode responseBody = objectMapper.readTree(response.getBody());

                // V2에서는 "response" 필드 없이 바로 "paymentId", "status", "totalAmount" 등이 루트에 존재
                int actualAmount = responseBody.get("totalAmount").asInt();
                String status = responseBody.get("status").asText();

                // 결제 성공(paid) && 금액 일치
                if ("paid".equals(status) && actualAmount == expectedAmount) {
                    // DB에 결제 상태 업데이트
                    paymentMapper.updatePaymentStatusByPortoneId(portonePaymentId, "completed");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ PortOne 결제 검증 실패: " + e.getMessage());
        }
        return false;
    }

}
