package com.care.project.main.controller;

import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("member")
public class ScheduleController {

@Autowired ScheduleService scheduleser;
	@GetMapping("/schedule")
	public ResponseEntity<?> getSchedule(@RequestParam("title") String title) {
		try {
			Map<String, Object> scheduleData = scheduleser.scheduleDetails(title);
			System.out.println("scheduleData : " + scheduleData);
            return CommonResponse.createResponse(
                    CommonResponse.builder()
                            .code(Constant.Success.SUCCESS_CODE)
                            .message("Success")
                            .data(scheduleData)
                            .build(),
                    HttpStatus.OK
            );

        } catch (Exception e) {
            log.info("scheduleData Error ");
            e.printStackTrace();

            return CommonResponse.createResponse(
                    CommonResponse.builder()
                            .code(ErrorType.ETC_FAIL.getErrorCode())
                            .message(ErrorType.ETC_FAIL.getErrorMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
		
	}
	
}
