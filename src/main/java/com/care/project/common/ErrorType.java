package com.care.project.common;

public enum ErrorType {
    ETC_FAIL(1999, "서버 내부 오류로 실패했습니다."), // 그 외 오류
    INVALID_PARAMETER(1001, "잘못된 요청 파라미터입니다."), // 잘못된 파라미터
    SERVER_ERROR(1002, "서버 내부 오류가 발생했습니다."); // 서버 오류

    private final int errorCode;
    private final String errorMessage;

    ErrorType(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
