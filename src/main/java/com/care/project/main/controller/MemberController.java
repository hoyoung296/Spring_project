package com.care.project.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.care.project.common.CommonResponse;
import com.care.project.common.Constant;
import com.care.project.common.ErrorType;
import com.care.project.main.dto.MemberDTO;
import com.care.project.main.service.MemberService;

@RestController
@CrossOrigin(origins = "*")
public class MemberController {
    @Autowired
    private MemberService ms;

    // 회원가입
    @PostMapping(value = "/register", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> register(@RequestBody MemberDTO memberDTO) {
        try {
            // 유효성 검사
            if (!ms.isUserIdValid(memberDTO.getUserId())) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "아이디는 6자 이상 영문자와 숫자만 가능합니다.");
            }
            if (!ms.isEmailValid(memberDTO.getEmail())) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "올바른 이메일 형식을 입력해주세요. (예: example@email.com)");
            }
            if (!ms.isPhoneNumberValid(memberDTO.getPhoneNumber())) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "하이픈(-)이나 공백 없이 숫자만 입력해주세요. (예: 01012345678)");
            }
            if (!ms.isPasswordValid(memberDTO.getPassword())) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "비밀번호는 최소 8자 이상이어야 하며, 영문/숫자/특수문자를 포함해야 합니다.");
            }

            if (ms.isUserIdDuplicate(memberDTO.getUserId())) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "등록된 아이디입니다.");
            }
            if (ms.isEmailDuplicate(memberDTO.getEmail())) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "등록된 이메일입니다.");
            }
            ms.registerMember(memberDTO);

            // 성공 응답
            CommonResponse<MemberDTO> response = CommonResponse.<MemberDTO>builder()
                    .code(Constant.Success.SUCCESS_CODE)
                    .message("회원가입이 완료되었습니다.")
                    .data(memberDTO)
                    .build();
            return CommonResponse.createResponse(response, HttpStatus.OK);

        } catch (Exception e) {
            return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
        }
    }

    // 아이디 중복 체크
    @GetMapping("/check-id")
    public ResponseEntity<?> checkUserId(@RequestParam String userId) {
        try {
            boolean isDuplicate = ms.isUserIdDuplicate(userId);
            CommonResponse<Boolean> response = CommonResponse.<Boolean>builder()
                    .code(Constant.Success.SUCCESS_CODE)
                    .message(isDuplicate ? "이미 등록된 아이디입니다." : "사용 가능한 아이디입니다.")
                    .data(isDuplicate)
                    .build();
            return CommonResponse.createResponse(response, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
        }
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            boolean isDuplicate = ms.isEmailDuplicate(email);
            CommonResponse<Boolean> response = CommonResponse.<Boolean>builder()
                    .code(Constant.Success.SUCCESS_CODE)
                    .message(isDuplicate ? "이미 등록된 이메일입니다." : "사용 가능한 이메일입니다.")
                    .data(isDuplicate)
                    .build();
            return CommonResponse.createResponse(response, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
        }
    }

    // 비밀번호 확인
    @PostMapping(value = "/check-password", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> checkPassword(@RequestBody MemberDTO memberDTO) {
        try {
            boolean isValid = ms.checkPassword(memberDTO);
            String message = isValid ? "비밀번호 확인 성공" : "비밀번호가 일치하지 않습니다.";
            CommonResponse<String> response = CommonResponse.<String>builder()
                    .code(Constant.Success.SUCCESS_CODE)
                    .message(message)
                    .build();
            return CommonResponse.createResponse(response, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
        }
    }

    // 로그인
    @PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> login(@RequestBody MemberDTO memberDTO) {
        try {
            boolean isValid = ms.loginMember(memberDTO);
            String message = isValid ? "로그인 성공" : "아이디 또는 비밀번호가 일치하지 않습니다.";
            
            MemberDTO updatedMemberDTO = ms.getMember(memberDTO.getUserId());
            
            CommonResponse<MemberDTO> response = CommonResponse.<MemberDTO>builder()
                    .code(Constant.Success.SUCCESS_CODE)
                    .message(message)
                    .data(updatedMemberDTO)
                    .build();
            return CommonResponse.createResponse(response, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
        }
    }

    // 회원정보 수정
    @PutMapping(value = "/update", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> updateMember(@RequestBody MemberDTO memberDTO) {
        try {
            if (!ms.checkPassword(memberDTO)) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "비밀번호가 일치하지 않습니다.");
            }
            // 유효성 검사
            if (!ms.isUserIdValid(memberDTO.getUserId())) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "아이디는 6자 이상 영문자와 숫자만 가능합니다.");
            }
            if (!ms.isEmailValid(memberDTO.getEmail())) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "올바른 이메일 형식을 입력해주세요. (예: example@email.com)");
            }
            if (!ms.isPhoneNumberValid(memberDTO.getPhoneNumber())) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "하이픈(-)이나 공백 없이 숫자만 입력해주세요. (예: 01012345678)");
            }
            // 새 비밀번호 유효성 검사 추가
            if (memberDTO.getNewPassword() != null && !memberDTO.getNewPassword().isEmpty()) {
                if (!ms.isPasswordValid(memberDTO.getNewPassword())) {
                    return createErrorResponse(ErrorType.INVALID_PARAMETER, "비밀번호는 최소 8자 이상이어야 하며, 영문/숫자/특수문자를 포함해야 합니다.");
                }
            }

            boolean isUpdated = ms.updateMember(memberDTO);
            String message = isUpdated ? "회원정보가 수정되었습니다." : "회원정보 수정 실패";
            CommonResponse<MemberDTO> response = CommonResponse.<MemberDTO>builder()
                    .code(Constant.Success.SUCCESS_CODE)
                    .message(message)
                    .data(memberDTO)
                    .build();
            return CommonResponse.createResponse(response, HttpStatus.OK);

        } catch (Exception e) {
            return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
        }
    }

    // 회원 탈퇴
    @DeleteMapping(value = "/delete", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> deleteMember(@RequestBody MemberDTO memberDTO) {
        try {
            if (!ms.checkPassword(memberDTO)) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "비밀번호가 일치하지 않습니다.");
            }
            boolean isDeleted = ms.deleteMember(memberDTO.getUserId());
            String message = isDeleted ? "회원탈퇴가 완료되었습니다." : "회원탈퇴 실패";
            CommonResponse<String> response = CommonResponse.<String>builder()
                    .code(Constant.Success.SUCCESS_CODE)
                    .message(message)
                    .build();
            return CommonResponse.createResponse(response, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
        }
    }

    // 사용자 정보 조회
    @GetMapping(value = "/info", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> getMemberInfo(@RequestParam String userId) {
        try {
            MemberDTO memberDTO = ms.getMember(userId);
            
         // 만약 회원 정보가 없다면 오류 처리
            if (memberDTO == null) {
                return createErrorResponse(ErrorType.INVALID_PARAMETER, "해당 회원을 찾을 수 없습니다.");
            }
            
            CommonResponse<MemberDTO> response = CommonResponse.<MemberDTO>builder()
                    .code(Constant.Success.SUCCESS_CODE)
                    .message("회원 정보 조회 성공")
                    .data(memberDTO)
                    .build();
            return CommonResponse.createResponse(response, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
        }
    }

    // 공통 에러 응답 생성 메서드
    private ResponseEntity<?> createErrorResponse(ErrorType errorType, String message) {
        CommonResponse<String> response = CommonResponse.<String>builder()
                .code(errorType.getErrorCode())
                .message(message)
                .build();
        return CommonResponse.createResponse(response, HttpStatus.BAD_REQUEST);
    }
}
