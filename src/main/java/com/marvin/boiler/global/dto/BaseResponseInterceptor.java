package com.marvin.boiler.global.dto;

import com.marvin.boiler.global.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 모든 API 응답을 가로채서 공통 규격(BaseResponse)으로 변환하고 상태 코드를 설정합니다.
 * 또한 성공 메시지(i18n) 자동 변환 처리를 담당합니다.
 */
@RestControllerAdvice(basePackages = "com.marvin.boiler.domain")
@RequiredArgsConstructor
public class BaseResponseInterceptor implements ResponseBodyAdvice<Object> {

    private final MessageUtils messageUtils;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        // 1. 이미 BaseResponse 규격인 경우
        if (body instanceof BaseResponse<?> baseResponse) {
            response.setStatusCode(baseResponse.httpStatus());

            // 성공 메시지가 키값으로 전달된 경우 다국어 처리
            if (baseResponse.success() && StringUtils.hasText(baseResponse.message())) {
                String translatedMessage = messageUtils.getMessage(baseResponse.message());
                // record는 불변이므로 새로운 객체 생성
                return new BaseResponse<>(
                        baseResponse.httpStatus(),
                        baseResponse.success(),
                        translatedMessage,
                        baseResponse.data(),
                        baseResponse.error()
                );
            }
            return body;
        }

        // 2. String 타입 예외 처리
        if (body instanceof String) {
            return body;
        }

        // 3. 그 외 일반 객체 자동 래핑 (기본 성공 응답)
        HttpStatus status = HttpStatus.OK;
        response.setStatusCode(status);

        return BaseResponse.ok(body);
    }
}
