package com.marvin.boiler.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 전역 에러 코드 정의
 * message 필드는 i18n/errors_*.properties의 메시지 키값으로 사용됩니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common (C)
    INTERNAL_SERVER_ERROR("C001", HttpStatus.INTERNAL_SERVER_ERROR, "error.internal_server_error"),
    REQUEST_ARGUMENT_NOT_VALID("C002", HttpStatus.BAD_REQUEST, "error.bad_request"),
    NOT_FOUND_END_POINT("C003", HttpStatus.NOT_FOUND, "error.common.not_found_end_point"),

    // Account (A)
    ACCOUNT_NOT_FOUND("A001", HttpStatus.BAD_REQUEST, "error.account.not_found"),
    ACCOUNT_EXISTS("A002", HttpStatus.BAD_REQUEST, "error.account.exists"),

    // END ENUM
    TEST_ERROR("C999", HttpStatus.INTERNAL_SERVER_ERROR, "error.test");


    private final String code;
    private final HttpStatus httpStatus;
    private final String message; // 메시지 키값
}
