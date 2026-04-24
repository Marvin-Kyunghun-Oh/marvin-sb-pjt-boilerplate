package com.marvin.boiler.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 모든 요청에 대해 고유한 Trace ID를 부여하는 필터
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 가장 먼저 실행되어야 함
public class MdcFilter implements Filter {

    private static final String TRACE_ID_NAME = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. 8자리의 짧은 UUID 생성 (로그 가독성)
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        
        // 2. MDC에 저장
        MDC.put(TRACE_ID_NAME, traceId);

        try {
            // 3. 응답 헤더에도 Trace ID 추가 (클라이언트 전달용)
            if (response instanceof HttpServletResponse httpServletResponse) {
                httpServletResponse.setHeader("X-Trace-Id", traceId);
            }
            
            chain.doFilter(request, response);
        } finally {
            // 4. 요청 종료 후 반드시 MDC 초기화 (ThreadLocal 오염 방지)
            MDC.remove(TRACE_ID_NAME);
        }
    }
}
