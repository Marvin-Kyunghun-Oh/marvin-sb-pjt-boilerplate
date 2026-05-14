package com.marvin.boiler.global.security;

import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * 인가 실패 처리 (403 Forbidden)
 * 전략 B: HandlerExceptionResolver를 사용하여 GlobalExceptionHandler로 예외 처리를 위임함.
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;

    public CustomAccessDeniedHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.debug("인가 실패(403) 발생: AccessDeniedHandler 진입");

        // 403 에러를 GlobalExceptionHandler로 위임 (ErrorCode.AUTH_FORBIDDEN 사용)
        resolver.resolveException(request, response, null, new BizException(ErrorCode.AUTH_FORBIDDEN));
    }
}
