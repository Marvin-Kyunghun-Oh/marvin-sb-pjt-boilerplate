package com.marvin.boiler.global.security;

import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * 인증 실패 처리 (401 Unauthorized)
 * 전략 B: HandlerExceptionResolver를 사용하여 GlobalExceptionHandler로 예외 처리를 위임함.
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public CustomAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        log.debug("인증 실패(401) 발생: EntryPoint 진입");

        // JwtFilter에서 저장한 에러 코드가 있는지 확인
        Object exception = request.getAttribute("exception");

        if (exception instanceof ErrorCode errorCode) {
            log.debug("JwtFilter로부터 전달받은 에러 코드로 위임: {}", errorCode);
            resolver.resolveException(request, response, null, new BizException(errorCode));
            return;
        }

        // 전달받은 에러 코드가 없다면 기본 인증 실패(AUTH_UNAUTHORIZED)로 처리
        log.debug("전달받은 에러 코드 없음 - 기본 인증 실패 위임");
        resolver.resolveException(request, response, null, new BizException(ErrorCode.AUTH_UNAUTHORIZED));
    }
}
