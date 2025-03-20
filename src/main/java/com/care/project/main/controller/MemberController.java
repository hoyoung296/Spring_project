package com.care.project.main.controller;

import javax.servlet.http.HttpSession;

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
import com.care.project.main.dto.LoginResponseDto;
import com.care.project.main.dto.MemberDTO;
import com.care.project.main.service.MemberService;

@RestController
@CrossOrigin(origins = "*")
public class MemberController {
	@Autowired
	private MemberService ms;

// 회원가입
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody MemberDTO memberDTO, HttpSession session) {
		try {
			String sessionId = session.getId(); // 세션 ID 가져오기
			System.out.println("현재 세션 ID: " + sessionId);
			boolean isEmailVerified = false;
			// ✅ 1. 이메일 인증 여부 확인
			if (sessionId != null) {
				isEmailVerified = true;
			}

			// 이메일 인증 여부 로그 추가
			System.out.println("이메일 인증 여부: " + isEmailVerified); // 여기서 확인 가능

			if (!isEmailVerified) {
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "이메일 인증을 완료해야 회원가입이 가능합니다.");
			}

			// 유효성 검사
			if (!ms.isEmailValid(memberDTO.getUserId())) { // 아이디를 이메일 형식으로 검사
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "아이디는 이메일 형식이어야 합니다. (예: example@email.com)");
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

			// 비밀번호 확인 추가
			if (!memberDTO.getPassword().equals(memberDTO.getConfirmPassword())) {
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
			}

			if (ms.isUserIdDuplicate(memberDTO.getUserId())) {
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "사용중인 아이디입니다.");
			}
			if (ms.isEmailDuplicate(memberDTO.getEmail())) {
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "사용중인 이메일입니다.");
			}

			ms.registerMember(memberDTO);

			// ✅ 5. 세션에서 인증 정보 삭제 (보안 강화)
			session.removeAttribute("isEmailVerified");

			// confirmPassword 제거 후 응답 반환
			memberDTO.setConfirmPassword(null);

			// 성공 응답
			CommonResponse<MemberDTO> response = CommonResponse.<MemberDTO>builder().code(Constant.Success.SUCCESS_CODE)
					.message("회원가입이 완료되었습니다.").data(memberDTO).build();
			return CommonResponse.createResponse(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
		}
	}

	// 아이디 중복 체크
	@GetMapping("/check-id")
	public ResponseEntity<?> checkUserId(@RequestParam String userId) {
		try {
			boolean isDuplicate = ms.isUserIdDuplicate(userId);
			CommonResponse<Boolean> response = CommonResponse.<Boolean>builder().code(Constant.Success.SUCCESS_CODE)
					.message(isDuplicate ? "이미 등록된 아이디입니다." : "사용 가능한 아이디입니다.").data(isDuplicate).build();
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
			CommonResponse<Boolean> response = CommonResponse.<Boolean>builder().code(Constant.Success.SUCCESS_CODE)
					.message(isDuplicate ? "이미 등록된 이메일입니다." : "사용 가능한 이메일입니다.").data(isDuplicate).build();
			return CommonResponse.createResponse(response, HttpStatus.OK);
		} catch (Exception e) {
			return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
		}
	}

	// 비밀번호 확인
	@PostMapping("/check-password")
	public ResponseEntity<?> checkPassword(@RequestBody MemberDTO memberDTO) {
		try {
			boolean isValid = ms.checkPassword(memberDTO);
			System.out.println("결과확인 : " + isValid);
			int num = isValid ? 1 : 0;
			String message = isValid ? "비밀번호 확인 성공" : "비밀번호가 일치하지 않습니다.";
			CommonResponse<String> response = CommonResponse.<String>builder().code(num).message(message).build();
			return CommonResponse.createResponse(response, HttpStatus.OK);
		} catch (Exception e) {
			return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
		}
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody MemberDTO memberDTO) {
		try {
			LoginResponseDto isValid = ms.loginMember(memberDTO);
			String message;

			// 로그인 성공 시 사용자 이름을 포함하여 메시지 수정
			if (isValid != null) {
				System.out.println("isValid" + isValid);
				message = "로그인 성공"; // 동적 메시지 생성
			} else {
				message = "아이디 또는 비밀번호가 일치하지 않습니다.";
			}
			CommonResponse<LoginResponseDto> response = CommonResponse.<LoginResponseDto>builder()
					.code(Constant.Success.SUCCESS_CODE).message(message).data(isValid) // updatedMemberDTO가 null일 수 있음
					.build();

			return CommonResponse.createResponse(response, HttpStatus.OK);

		} catch (Exception e) {
			return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
		}
	}

	// 아이디 찾기
	@PostMapping("/findId")
	public ResponseEntity<?> findUserId(@RequestBody MemberDTO memberDTO) {
		String phoneNumber = ms.findUserId(memberDTO);
		if (phoneNumber == null) {
			return createErrorResponse(ErrorType.INVALID_PARAMETER, "입력한 정보와 일치하는 전화번호가 없습니다.");
		}
		return ResponseEntity.ok(phoneNumber);
	}


	// 회원정보 수정
	@PutMapping("/update")
	public ResponseEntity<?> updateMember(@RequestBody MemberDTO memberDTO) {
		System.out.println("받은 회원정보: " + memberDTO);
	    System.out.println("받은 우편번호: " + memberDTO.getPostNum());
		try {
			if (!ms.newcheckPassword(memberDTO)) {
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "비밀번호가 일치하지 않습니다.");
			}

			// 새로운 비밀번호 확인 추가
			if (!memberDTO.getNewPassword().equals(memberDTO.getConfirmPassword())) {
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "새로운 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
			}

			// 유효성 검사
			if (!ms.isUserIdValid(memberDTO.getUserId())) {
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "아이디는 6자 이상 영문자와 숫자만 가능합니다.");
			}
			// 이메일 유효성 검사 제외 (기존 값으로 유지 처리)
			if (memberDTO.getEmail() != null && !memberDTO.getEmail().isEmpty()) {
				if (!ms.isEmailValid(memberDTO.getEmail())) {
					return createErrorResponse(ErrorType.INVALID_PARAMETER,
							"올바른 이메일 형식을 입력해주세요. (예: example@email.com)");
				}
			}

			// 폰번호 유효성 검사 제외 (기존 값으로 유지 처리)
			if (memberDTO.getPhoneNumber() != null && !memberDTO.getPhoneNumber().isEmpty()) {
				if (!ms.isPhoneNumberValid(memberDTO.getPhoneNumber())) {
					return createErrorResponse(ErrorType.INVALID_PARAMETER,
							"하이픈(-)이나 공백 없이 숫자만 입력해주세요. (예: 01012345678)");
				}
			}
			// 새 비밀번호 유효성 검사 추가
			if (memberDTO.getNewPassword() != null && !memberDTO.getNewPassword().isEmpty()) {
				if (!ms.isPasswordValid(memberDTO.getNewPassword())) {
					return createErrorResponse(ErrorType.INVALID_PARAMETER,
							"비밀번호는 최소 8자 이상이어야 하며, 영문/숫자/특수문자를 포함해야 합니다.");
				}
			}

			boolean isUpdated = ms.updateMember(memberDTO);
			String message = isUpdated ? "회원정보가 수정되었습니다." : "회원정보 수정 실패";
			CommonResponse<MemberDTO> response = CommonResponse.<MemberDTO>builder().code(Constant.Success.SUCCESS_CODE)
					.message(message).data(memberDTO).build();
			return CommonResponse.createResponse(response, HttpStatus.OK);

		} catch (Exception e) {
			return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
		}
	}
	

	// 회원 탈퇴
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteMember(@RequestBody MemberDTO memberDTO) {
		try {
			if (!ms.newcheckPassword(memberDTO)) {
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "비밀번호가 일치하지 않습니다.");
			}
			boolean isDeleted = ms.deleteMember(memberDTO.getUserId());
			String message = isDeleted ? "회원탈퇴가 완료되었습니다." : "회원탈퇴 실패";
			CommonResponse<String> response = CommonResponse.<String>builder().code(Constant.Success.SUCCESS_CODE)
					.message(message).build();
			return CommonResponse.createResponse(response, HttpStatus.OK);
		} catch (Exception e) {
			return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
		}
	}

	// 사용자 정보 조회
	@GetMapping("/info")
	public ResponseEntity<?> getMemberInfo(@RequestParam String userId) {
		try {
			MemberDTO memberDTO = ms.getMember(userId);

			// 만약 회원 정보가 없다면 오류 처리
			if (memberDTO == null) {
				return createErrorResponse(ErrorType.INVALID_PARAMETER, "해당 회원을 찾을 수 없습니다.");
			}
			
			CommonResponse<MemberDTO> response = CommonResponse.<MemberDTO>builder().code(Constant.Success.SUCCESS_CODE)
					.message("회원 정보 조회 성공").data(memberDTO).build();
			return CommonResponse.createResponse(response, HttpStatus.OK);
		} catch (Exception e) {
			return createErrorResponse(ErrorType.SERVER_ERROR, "서버 내부 오류로 실패했습니다.");
		}
	}

	// 공통 에러 응답 생성 메서드
	private ResponseEntity<?> createErrorResponse(ErrorType errorType, String message) {
		CommonResponse<String> response = CommonResponse.<String>builder().code(errorType.getErrorCode())
				.message(message).build();
		return CommonResponse.createResponse(response, HttpStatus.BAD_REQUEST);
	}
}