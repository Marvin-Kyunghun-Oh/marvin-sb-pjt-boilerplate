package com.marvin.boiler.global.exception;

import com.marvin.boiler.global.utils.MessageUtils;
import com.marvin.boiler.global.dto.BaseResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageUtils messageUtils;

    /**
     * 권한 거부 예외 처리 (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access Denied: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.AUTH_FORBIDDEN;
        String message = messageUtils.getMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(errorCode, ExceptionDto.of(errorCode, message, null)));
    }

    /**
     * Bean Validation (@Valid) 유효성 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.REQUEST_ARGUMENT_NOT_VALID;
        String message = messageUtils.getMessage(errorCode.getMessage());
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(errorCode, ExceptionDto.of(errorCode, message, e.getBindingResult(), null)));
    }

    /**
     * Spring 6+ 파라미터 유효성 검증 실패
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseResponse<?>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.warn("HandlerMethodValidationException: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.REQUEST_ARGUMENT_NOT_VALID;
        String message = messageUtils.getMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(errorCode, ExceptionDto.of(errorCode, message, e, null)));
    }

    /**
     * ConstraintViolationException (@Validated) 유효성 검증 실패
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Constraint violation: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.REQUEST_ARGUMENT_NOT_VALID;
        String message = messageUtils.getMessage(errorCode.getMessage());
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(errorCode, ExceptionDto.of(errorCode, message, null)));
    }

    /**
     * 존재하지 않는 API 요청 (404) 또는 지원하지 않는 HTTP Method (405)
     */
    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<BaseResponse<?>> handleNotFoundException(Exception e) {
        log.warn("Not found or Method not supported: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.NOT_FOUND_END_POINT;
        String message = messageUtils.getMessage(errorCode.getMessage());
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(errorCode, ExceptionDto.of(errorCode, message, null)));
    }

    /**
     * 비즈니스 예외 처리 (Custom Exception)
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<BaseResponse<?>> handleBizException(BizException e) {
        ErrorCode errorCode = e.getErrorCode();
        String message = messageUtils.getMessage(errorCode.getMessage());
        log.error("Business Exception [{}]: {}", errorCode.getCode(), message);
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(errorCode, ExceptionDto.of(errorCode, message, null)));
    }

    /**
     * 그 외 정의되지 않은 모든 서버 내부 예외 처리 (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        log.error("Unexpected Internal Server Error: ", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        String message = messageUtils.getMessage(errorCode.getMessage());
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(errorCode, ExceptionDto.of(errorCode, message, null)));
    }
}
