package com.marvin.boiler.global.dto;

import com.marvin.boiler.global.exception.ExceptionDto;
import com.marvin.boiler.global.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
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
@RestControllerAdvice(basePackages = {"com.marvin.boiler.domain", "com.marvin.boiler.global"})
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

            String finalMessage = baseResponse.message();
            ExceptionDto finalError = baseResponse.error();
            boolean changed = false;

            // [성공 응답] 메시지가 키값으로 전달된 경우 다국어 처리
            if (baseResponse.success() && StringUtils.hasText(baseResponse.message())) {
                finalMessage = messageUtils.getMessage(baseResponse.message());
                changed = true;
            }

            // [에러 응답] MDC에서 Trace ID를 꺼내 에러 객체에 주입
            // 이를 통해 클라이언트 응답과 서버 로그를 Trace ID 하나로 연결할 수 있습니다.
            if (!baseResponse.success() && baseResponse.error() != null) {
                // MDC에서 traceId를 가져오되, 값이 없으면(테스트 등) 즉석에서 생성
                String traceId = MDC.get("traceId");
                if (!StringUtils.hasText(traceId)) {
                    traceId = java.util.UUID.randomUUID().toString().substring(0, 8);
                }
                
                // record는 불변이므로 새로운 ExceptionDto 생성 (traceId 주입)
                finalError = new ExceptionDto(
                        baseResponse.error().code(),
                        baseResponse.error().message(),
                        baseResponse.error().errors(),
                        traceId
                );
                changed = true;
            }

            // 변경사항이 있을 경우에만 새로운 BaseResponse 객체 생성
            return changed
                    ? new BaseResponse<>(
                        baseResponse.httpStatus(),
                        baseResponse.success(),
                        finalMessage,
                        baseResponse.data(),
                        finalError)
                    : body;
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
