package com.marvin.boiler.global.dto;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 모든 API 응답을 가로채서 공통 규격(BaseResponse)으로 변환하고 상태 코드를 설정합니다.
 */
@RestControllerAdvice(basePackages = "com.marvin.boiler.domain")
public class BaseResponseInterceptor implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 특정 타입이나 어노테이션을 제외하고 싶을 때 조건을 추가할 수 있습니다.
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        // 1. 이미 BaseResponse 규격인 경우 (주로 에러 응답이나 명시적 반환)
        if (body instanceof BaseResponse<?> baseResponse) {
            response.setStatusCode(baseResponse.httpStatus());
            return body;
        }

        // 2. String 타입은 Jackson이 아닌 StringHttpMessageConverter를 타기 때문에 
        // 자동 래핑 시 별도 처리가 필요할 수 있으나, 여기서는 DTO 중심의 설계를 가정합니다.
        if (body instanceof String) {
            return body; 
        }

        // 3. 그 외 모든 일반 객체(DTO)를 성공 규격으로 자동 래핑
        // 이 로직 덕분에 컨트롤러에서 BaseResponse.ok()를 일일이 감쌀 필요가 없어집니다.
        HttpStatus status = HttpStatus.OK;
        response.setStatusCode(status);

        return BaseResponse.ok(body);
    }
}
