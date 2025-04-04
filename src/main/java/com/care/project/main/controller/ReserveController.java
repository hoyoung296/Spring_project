package com.care.project.main.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.common.CommonResponse;
import com.care.project.common.Constant;
import com.care.project.common.ErrorType;
import com.care.project.main.service.ReserveService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("member/reserve")
public class ReserveController {

	@Autowired
	ReserveService reserver;

	@PostMapping("/reservation")
	public ResponseEntity<?> Reservation(HttpServletRequest req, @RequestBody Map<String, Object> requestData) {

		try {
			// ✅ 1. 요청 데이터 유효성 검사
			if (requestData == null) {
				throw new IllegalArgumentException("요청 데이터가 누락되었습니다.");
			}

			// ✅ 2. 필수 파라미터 확인
			if (!requestData.containsKey("scheduleId") || !requestData.containsKey("seatIds")
					|| !requestData.containsKey("totalAmount")) {
				throw new IllegalArgumentException("필수 요청 파라미터가 누락되었습니다.");
			}

			// ✅ 3.userId는 세션이 아닌 requestBody에서 가져옴
			String userId = (String) requestData.get("userId");
			if (userId == null) {
				userId = "aaa"; // 기본값
			}

			// ✅ 4. 안전하게 값 추출
			int scheduleid = Integer.parseInt(String.valueOf(requestData.get("scheduleId")));
			List<String> seatIds = (List<String>) requestData.get("seatIds");
			int totalAmount = Integer.parseInt(String.valueOf(requestData.get("totalAmount")));

			System.out.println("✅ 요청 데이터: " + requestData);
			System.out.println("✅ 유저 ID: " + userId);
			System.out.println("✅ 스케줄 ID: " + scheduleid);
			System.out.println("✅ 선택한 좌석: " + seatIds);
			System.out.println("✅ 총 결제 금액: " + totalAmount);

			// ✅ 5. 예매 서비스 호출
			Long reservationId = reserver.createReservation(userId, scheduleid, totalAmount);

			// ✅ 6. 좌석 상태 및 예약 처리
			if (reservationId != null) {
				List<Integer> seatStatusIds = reserver.seatStatus(scheduleid, seatIds);

				if (seatStatusIds != null && !seatStatusIds.isEmpty()) {
					boolean updateSuccess = reserver.updateSeatStatusType(seatStatusIds);

					if (updateSuccess) {
						boolean isSuccess = reserver.reserveSeats(reservationId, seatStatusIds);
						if (!isSuccess) {
							throw new RuntimeException("좌석 예약 중 문제가 발생했습니다.");
						}
					} else {
						throw new RuntimeException("좌석 상태 업데이트 실패.");
					}
				} else {
					throw new RuntimeException("좌석 상태 ID를 가져오는 데 실패했습니다.");
				}
			} else {
				throw new RuntimeException("예약 ID 생성 실패.");
			}

			// ✅ 7. 응답 데이터 구성
			Map<String, Object> responseData = new HashMap<>();
			responseData.put("message", "예매가 성공적으로 완료되었습니다.");
			responseData.put("reservationId", reservationId.toString());
			responseData.put("userId", userId);
			responseData.put("scheduleId", scheduleid);
			responseData.put("seatIds", seatIds);
			responseData.put("totalAmount", totalAmount);

			// ✅ 8. CommonResponse로 응답 반환
			return CommonResponse.createResponse(CommonResponse.builder().code(Constant.Success.SUCCESS_CODE)
					.message("Success").data(responseData).build(), HttpStatus.OK);

		} catch (NullPointerException e) {
			System.err.println("❌ NullPointerException 발생: " + e.getMessage());
			return CommonResponse.createResponse(
					CommonResponse.builder().code(ErrorType.ETC_FAIL.getErrorCode()).message("필수 값이 누락되었습니다.").build(),
					HttpStatus.BAD_REQUEST);

		} catch (IllegalArgumentException e) {
			System.err.println("❌ 잘못된 요청: " + e.getMessage());
			return CommonResponse.createResponse(CommonResponse.builder()
					.code(ErrorType.INVALID_PARAMETER.getErrorCode()).message(e.getMessage()).build(),
					HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			System.err.println("❌ 서버 내부 오류 발생: " + e.getMessage());
			return CommonResponse.createResponse(CommonResponse.builder().code(ErrorType.SERVER_ERROR.getErrorCode())
					.message("서버 내부 오류가 발생했습니다.").build(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ✅ 예약 취소 API
	@DeleteMapping("/cancel")
	public ResponseEntity<?> cancelReservation(@RequestBody Map<String, Object> reserveData) {

		try {
			// ✅ 요청 데이터 유효성 검사
			if (reserveData == null) {
				throw new IllegalArgumentException("요청 데이터가 누락되었습니다.");
			}

			// ✅ 필수 파라미터 확인
			if (!reserveData.containsKey("scheduleId") || !reserveData.containsKey("seatIds")
					|| !reserveData.containsKey("reservationId")) {
				throw new IllegalArgumentException("필수 요청 파라미터가 누락되었습니다.");
			}

			Long reservationId = Long.parseLong(String.valueOf(reserveData.get("reservationId")));
			int scheduleId = Integer.parseInt(String.valueOf(reserveData.get("scheduleId")));
			List<String> seatIds = (List<String>) reserveData.get("seatIds");
			List<Integer> seatStatusIds = reserver.seatStatus(scheduleId, seatIds);

			boolean isDeleted = reserver.cancelReservation(reservationId, scheduleId, seatStatusIds);
			if (isDeleted) {
				System.out.println("✅ 예약이 성공적으로 취소되었습니다. 예약 ID: " + reservationId);
				return ResponseEntity.ok(Map.of("message", "예약이 성공적으로 취소되었습니다."));
			} else {
				throw new RuntimeException("예약 취소 실패.");
			}

		} catch (Exception e) {
			System.err.println("❌ 예약 취소 중 오류 발생: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "서버 내부 오류가 발생했습니다."));
		}
	}
}