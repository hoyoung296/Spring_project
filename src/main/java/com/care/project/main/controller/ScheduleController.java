package com.care.project.main.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.common.CommonResponse;
import com.care.project.common.Constant;
import com.care.project.common.ErrorType;
import com.care.project.main.dto.ScheduleDTO;
import com.care.project.main.service.ReserveService;
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
			Map<String, Object> scheduleInfo = scheduleser.scheduleInfo(startdate);
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

}
