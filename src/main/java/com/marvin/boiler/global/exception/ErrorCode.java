package com.marvin.boiler.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common (C)
    INTERNAL_SERVER_ERROR("C001", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    REQUEST_ARGUMENT_NOT_VALID("C002", HttpStatus.BAD_REQUEST, "잘못된 요청 양식입니다."),
    NOT_FOUND_END_POINT("C003", HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),
    
    // Account (A)
    ACCOUNT_NOT_FOUND("A001", HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    ACCOUNT_EXISTS("A002", HttpStatus.BAD_REQUEST, "이미 가입된 사용자입니다."),
    TEST_ERROR("A999", HttpStatus.BAD_REQUEST, "임의 에러입니다.");


    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}