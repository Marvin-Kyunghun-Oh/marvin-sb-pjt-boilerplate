package com.marvin.boiler.global.exception;

import com.marvin.boiler.global.dto.BaseResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bean Validation (@Valid) 유효성 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.REQUEST_ARGUMENT_NOT_VALID;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(errorCode, ExceptionDto.of(errorCode, e.getBindingResult())));
    }

    /**
     * Spring 6+ 파라미터 유효성 검증 실패
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseResponse<?>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.warn("HandlerMethodValidationException: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.REQUEST_ARGUMENT_NOT_VALID;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(errorCode, ExceptionDto.of(errorCode, e)));
    }

    /**
     * ConstraintViolationException (@Validated) 유효성 검증 실패
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Constraint violation: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.REQUEST_ARGUMENT_NOT_VALID;
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(new BizException(errorCode)));
    }

    /**
     * 존재하지 않는 API 요청 (404) 또는 지원하지 않는 HTTP Method (405)
     */
    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<BaseResponse<?>> handleNotFoundException(Exception e) {
        log.warn("Not found or Method not supported: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.NOT_FOUND_END_POINT;
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(new BizException(errorCode)));
    }

    /**
     * 비즈니스 예외 처리 (Custom Exception)
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<BaseResponse<?>> handleBizException(BizException e) {
        log.error("Business Exception [{}]: {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(BaseResponse.fail(e));
    }

    /**
     * 그 외 정의되지 않은 모든 서버 내부 예외 처리 (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        log.error("Unexpected Internal Server Error: ", e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(BaseResponse.fail(new BizException(errorCode)));
    }
}
