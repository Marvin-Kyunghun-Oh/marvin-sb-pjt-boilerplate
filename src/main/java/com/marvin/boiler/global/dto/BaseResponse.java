package com.marvin.boiler.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * 공통 응답 규격 (BaseResponse)
 *
 * @param httpStatus HTTP 상태 코드 (JSON 변환 시 제외)
 * @param success    성공 여부
 * @param data       응답 데이터
 * @param error      에러 정보 (임시 Exception 타입)
 * @param <T>        데이터 타입
 */
public record BaseResponse<T>(
        @JsonIgnore
        HttpStatus httpStatus,
        boolean success,
        @Nullable T data,
        @Nullable Exception error
) {

    public static <T> BaseResponse<T> ok(@Nullable final T data) {
        return new BaseResponse<>(HttpStatus.OK, true, data, null);
    }

    public static <T> BaseResponse<T> created(@Nullable final T data) {
        return new BaseResponse<>(HttpStatus.CREATED, true, data, null);
    }

    public static <T> BaseResponse<T> noContent() {
        return new BaseResponse<>(HttpStatus.NO_CONTENT, true, null, null);
    }

}
