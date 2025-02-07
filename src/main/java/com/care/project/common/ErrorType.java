package com.care.project.common;

public enum ErrorType {
	ETC_FAIL(1999, "서버 내부 오류로 실패했습니다."); // 그 외 오류;

	private int errorCode;
	private String errorMessage;

	ErrorType(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public Integer getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage(){
        return this.errorMessage;
	}
    
}
