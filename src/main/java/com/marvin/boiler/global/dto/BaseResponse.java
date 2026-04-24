package com.marvin.boiler.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import com.marvin.boiler.global.exception.ExceptionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * 공통 응답 규격 (BaseResponse)
 *
 * @param httpStatus HTTP 상태 코드 (JSON 변환 시 제외)
 * @param success    성공 여부
 * @param message    성공/실패 안내 메시지 (다국어 키 대응)
 * @param data       응답 데이터
 * @param error      에러 정보
 * @param <T>        데이터 타입
 */
@Schema(description = "공통 응답 규격")
public record BaseResponse<T>(
        @JsonIgnore
        @Schema(hidden = true)
        HttpStatus httpStatus,
        @Schema(description = "성공 여부", example = "true")
        boolean success,
        @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
        @Nullable String message,
        @Schema(description = "응답 데이터 (성공 시)")
        @Nullable T data,
        @Schema(description = "에러 정보 (실패 시)")
        @Nullable ExceptionDto error
) {

    // --- 성공 응답 팩토리 메서드 ---

    public static <T> BaseResponse<T> ok(@Nullable final T data) {
        return new BaseResponse<>(HttpStatus.OK, true, null, data, null);
    }

    public static <T> BaseResponse<T> ok(@Nullable final T data, String message) {
        return new BaseResponse<>(HttpStatus.OK, true, message, data, null);
    }

    public static <T> BaseResponse<T> created(@Nullable final T data) {
        return new BaseResponse<>(HttpStatus.CREATED, true, null, data, null);
    }

    public static <T> BaseResponse<T> created(@Nullable final T data, String message) {
        return new BaseResponse<>(HttpStatus.CREATED, true, message, data, null);
    }

    public static <T> BaseResponse<T> noContent() {
        return new BaseResponse<>(HttpStatus.NO_CONTENT, true, null, null, null);
    }

    // --- 실패 응답 팩토리 메서드 ---

    public static <T> BaseResponse<T> fail(final BizException e) {
        return new BaseResponse<>(e.getErrorCode().getHttpStatus(), false, null, null, ExceptionDto.of(e.getErrorCode(), null));
    }

    public static <T> BaseResponse<T> fail(final ErrorCode errorCode, final ExceptionDto exceptionDto) {
        return new BaseResponse<>(errorCode.getHttpStatus(), false, null, null, exceptionDto);
    }

}
