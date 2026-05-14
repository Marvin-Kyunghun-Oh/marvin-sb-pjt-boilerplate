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
    ACCOUNT_INVALID_NEWPASSWORD_PATTERN("A003", HttpStatus.BAD_REQUEST, "validation.password.pattern"),
    ACCOUNT_SAME_AS_OLD_PASSWORD("A004", HttpStatus.BAD_REQUEST, "error.account.same_as_old_password"),
    ACCOUNT_INVALID_PASSWORD("A005", HttpStatus.BAD_REQUEST, "error.account.invalid_password"),

    // Auth (B)
    AUTH_LOGIN_FAILED("B001", HttpStatus.BAD_REQUEST, "error.auth.login_failed"),
    AUTH_LOGIN_INVALID_STATUS("B002", HttpStatus.BAD_REQUEST, "error.auth.login_invalid_status"),
    AUTH_TOKEN_EXPIRED("B003", HttpStatus.UNAUTHORIZED, "error.auth.token_expired"),
    AUTH_TOKEN_INVALID("B004", HttpStatus.UNAUTHORIZED, "error.auth.token_invalid"),
    AUTH_UNAUTHORIZED("B005", HttpStatus.UNAUTHORIZED, "error.auth.unauthorized"),
    AUTH_FORBIDDEN("B006", HttpStatus.FORBIDDEN, "error.auth.forbidden"),

    // END ENUM
    TEST_ERROR("C999", HttpStatus.INTERNAL_SERVER_ERROR, "error.test");


    private final String code;
    private final HttpStatus httpStatus;
    private final String message; // 메시지 키값
}
