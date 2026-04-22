package com.marvin.boiler.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 에러 응답 상세 규격
 */
public record ExceptionDto(
        String code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<FieldError> errors
) {
    /**
     * 일반적인 에러 생성
     */
    public static ExceptionDto of(ErrorCode errorCode) {
        return new ExceptionDto(errorCode.getCode(), errorCode.getMessage(), List.of());
    }

    /**
     * 유효성 검증 실패(BindingResult) 시 에러 생성 (@Valid)
     */
    public static ExceptionDto of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ExceptionDto(
                errorCode.getCode(),
                errorCode.getMessage(),
                FieldError.from(bindingResult)
        );
    }

    /**
     * 유효성 검증 실패(HandlerMethodValidationException) 시 에러 생성 (Spring 6.1+)
     */
    public static ExceptionDto of(ErrorCode errorCode, HandlerMethodValidationException e) {
        List<FieldError> errors = e.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> {
                            String field = result.getMethodParameter().getParameterName();
                            String message = error.getDefaultMessage();
                            return new FieldError(field != null ? field : "", "", message);
                        }))
                .collect(Collectors.toList());

        return new ExceptionDto(errorCode.getCode(), errorCode.getMessage(), errors);
    }

    /**
     * 필드별 에러 상세 정보
     */
    public record FieldError(
            String field,
            String value,
            String reason
    ) {
        public static List<FieldError> from(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()
                    ))
                    .collect(Collectors.toList());
        }
    }
}
