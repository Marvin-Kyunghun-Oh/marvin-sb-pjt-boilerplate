package com.marvin.boiler.global.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestControllerAdvice(basePackages = "com.marvin.boiler.domain") // swagger나 actuator 응답을 패스하기 위해, API 도메인에만 응답 규격을 강제함.
@RequiredArgsConstructor
public class BaseResponseInterceptor implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // Swagger 관련 패키지나 특정 응답 타입을 제외하고 싶을 때 조건을 추가할 수 있습니다.
        String className = returnType.getContainingClass().getName();
        return !className.contains("springdoc") && !className.contains("swagger");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        // 1. 이미 BaseResponse 규격인 경우 (명시적 반환 또는 에러 응답)
        if (body instanceof BaseResponse<?> baseResponse) {
            response.setStatusCode(baseResponse.httpStatus());
            return body;
        }

        // 2. null 반환 처리 (void 메서드 등)
        if (body == null) {
            response.setStatusCode(HttpStatus.OK);
            return BaseResponse.ok(null);
        }

        // 3. String 타입 반환 처리
        // StringHttpMessageConverter는 객체가 아닌 문자열만 처리하므로, 
        // BaseResponse 객체를 직접 JSON String으로 변환해서 내보내야 ClassCastException을 방지할 수 있습니다.
        if (body instanceof String) {
            try {
                response.setStatusCode(HttpStatus.OK);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(BaseResponse.ok(body));
            } catch (JsonProcessingException e) {
                log.error("Failed to convert String to BaseResponse JSON", e);
                return body; // 실패 시 원본 문자열 반환
            }
        }

        // 4. 그 외 모든 객체(DTO) 자동 래핑
        response.setStatusCode(HttpStatus.OK);
        return BaseResponse.ok(body);
    }
}

