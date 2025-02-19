package com.care.project.main.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.common.CommonResponse;
import com.care.project.common.Constant;
import com.care.project.common.ErrorType;
import com.care.project.main.dto.ScheduleDTO;
import com.care.project.main.service.ScheduleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("member/schedule")
public class ScheduleController {

	@Autowired
	ScheduleService scheduleser;

	@GetMapping("/title")
	public ResponseEntity<?> getScheduleDate(@RequestParam("title") String title) {
		
		System.out.println("제목값:"+title);
		try {
			List<Map<String, Object>> scheduleData = scheduleser.scheduleDate(title);
			System.out.println("scheduleData : " + scheduleData);
			return CommonResponse.createResponse(CommonResponse.builder().code(Constant.Success.SUCCESS_CODE)
					.message("Success").data(scheduleData).build(), HttpStatus.OK);

		} catch (Exception e) {
			log.info("scheduleData Error ");
			e.printStackTrace();

			return CommonResponse.createResponse(CommonResponse.builder().code(ErrorType.ETC_FAIL.getErrorCode())
					.message(ErrorType.ETC_FAIL.getErrorMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/startdate")
	public ResponseEntity<?> getScheduleInfo(@RequestParam("startdate") String startdate) {
		try {
			List<Map<String, Object>> scheduleInfo = scheduleser.scheduleInfo(startdate);
			System.out.println("scheduleInfo : " + scheduleInfo);
			return CommonResponse.createResponse(CommonResponse.builder().code(Constant.Success.SUCCESS_CODE)
					.message("Success").data(scheduleInfo).build(), HttpStatus.OK);

		} catch (Exception e) {
			log.info("scheduleInfo Error ");
			e.printStackTrace();

			return CommonResponse.createResponse(CommonResponse.builder().code(ErrorType.ETC_FAIL.getErrorCode())
					.message(ErrorType.ETC_FAIL.getErrorMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/seatselect")
	public ResponseEntity<?> SeatSelect(@RequestParam("schedule_id") Integer scheduleid) {
		try {
			Map<String, Object> scheduleDetailData = scheduleser.scheduleDetailData(scheduleid);
			List<Map<String, Object>> reservedSeats  = scheduleser.reservedSeats(scheduleid);
			 // 최종 응답 데이터 구성
	        Map<String, Object> responseData = Map.of(
	            "scheduleDetailData", scheduleDetailData,
	            "reservedSeats", reservedSeats
	        );
			System.out.println("responseData : " + responseData);
            return CommonResponse.createResponse(
                    CommonResponse.builder()
                            .code(Constant.Success.SUCCESS_CODE)
                            .message("Success")
                            .data(responseData)
                            .build(),
                    HttpStatus.OK
            );

		} catch (Exception e) {
			log.info("scheduleInfo Error ");
			e.printStackTrace();

			return CommonResponse.createResponse(CommonResponse.builder().code(ErrorType.ETC_FAIL.getErrorCode())
					.message(ErrorType.ETC_FAIL.getErrorMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/reservation")
	public ResponseEntity<?> Reservation(
			HttpServletRequest req,
			@RequestBody Map<String, Object> requestData){

		//  세션에서 사용자 ID 가져오기
        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("userId") : "aaa"; // 기본값 "aaa"

        // 요청 데이터에서 값 추출
        int scheduleid = Integer.parseInt(requestData.get("scheduleid").toString());
        List<String> seatIds = (List<String>) requestData.get("seatIds");
        int totalAmount = Integer.parseInt(requestData.get("totalamount").toString());

        System.out.println("유저 ID: " + userId);
        System.out.println("스케줄 ID: " + scheduleid);
        System.out.println("선택한 좌석: " + seatIds);
        System.out.println("총 결제 금액: " + totalAmount);
		
        // 예매 서비스 호출
        Long reservationId = scheduleser.createReservation(userId, scheduleid, totalAmount);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "예매가 완료되었습니다.");
        response.put("reservationId", reservationId);
        response.put("userId", userId);
        response.put("scheduleId", scheduleid);
        response.put("seatIds", seatIds);
        response.put("totalAmount", totalAmount);

        return ResponseEntity.ok(response);
	}

}
