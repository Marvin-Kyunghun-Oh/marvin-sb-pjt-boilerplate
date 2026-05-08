package com.marvin.boiler.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 에러 응답 상세 규격
 */
@Schema(description = "에러 응답 상세 규격")
public record ExceptionDto(
        @Schema(description = "에러 코드 (예: C001, A001)", example = "C001")
        String code,
        @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
        String message,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @Schema(description = "필드별 에러 상세 목록 (유효성 검증 실패 시에만 포함)")
        List<FieldError> errors,
        @Schema(description = "Trace-ID(에러추적용)", example = "5f6141e7")
        String traceId
) {
    /**
     * 일반적인 에러 생성 (번역된 메시지 포함)
     */
    public static ExceptionDto of(ErrorCode errorCode, String translatedMessage, String traceId) {
        return new ExceptionDto(errorCode.getCode(), translatedMessage, List.of(), traceId);
    }

    /**
     * 유효성 검증 실패(BindingResult) 시 에러 생성 (@Valid)
     */
    public static ExceptionDto of(ErrorCode errorCode, String translatedMessage, BindingResult bindingResult, String traceId) {
        return new ExceptionDto(
                errorCode.getCode(),
                translatedMessage,
                FieldError.from(bindingResult),
                traceId
        );
    }

    /**
     * 유효성 검증 실패(HandlerMethodValidationException) 시 에러 생성 (Spring 6.1+)
     */
    public static ExceptionDto of(ErrorCode errorCode, String translatedMessage, HandlerMethodValidationException e, String traceId) {
        List<FieldError> errors = e.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> {
                            String field = result.getMethodParameter().getParameterName();
                            String message = error.getDefaultMessage();
                            return new FieldError(field != null ? field : "", "", message);
                        }))
                .collect(Collectors.toList());

        return new ExceptionDto(errorCode.getCode(), translatedMessage, errors, traceId);
    }

    /**
     * 필드별 에러 상세 정보
     */
    @Schema(description = "필드별 에러 상세 정보")
    public record FieldError(
            @Schema(description = "필드명", example = "email")
            String field,
            @Schema(description = "입력된 값", example = "invalid-email")
            String value,
            @Schema(description = "에러 사유", example = "이메일 형식이 올바르지 않습니다.")
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
