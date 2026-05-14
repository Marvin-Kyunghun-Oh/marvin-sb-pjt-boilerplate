package com.marvin.boiler.global.security;

import com.marvin.boiler.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터 - 1단계: 토큰 추출
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. [Extract] Request Header에서 토큰 추출
        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt)) {
            try {
                // 2. [Validate] 토큰 유효성 검증
                if (tokenProvider.validateToken(jwt)) {
                    // 3. [Set] 토큰에서 추출한 사용자정보를 SecurityContext에 담는다.
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("인증 성공: {}", authentication.getName());
                }
            } catch (SecurityException | MalformedJwtException e) {
                log.info("잘못된 JWT 서명입니다.");
                request.setAttribute("exception", ErrorCode.AUTH_TOKEN_INVALID);
            } catch (ExpiredJwtException e) {
                log.info("만료된 JWT 토큰입니다.");
                request.setAttribute("exception", ErrorCode.AUTH_TOKEN_EXPIRED);
            } catch (UnsupportedJwtException e) {
                log.info("지원되지 않는 JWT 토큰입니다.");
                request.setAttribute("exception", ErrorCode.AUTH_TOKEN_INVALID);
            } catch (IllegalArgumentException e) {
                log.info("JWT 토큰이 잘못되었습니다.");
                request.setAttribute("exception", ErrorCode.AUTH_TOKEN_INVALID);
            } catch (Exception e) {
                log.error("JWT 인증 중 예상치 못한 에러가 발생했습니다.");
                request.setAttribute("exception", ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        // 4. [Chain] 다음 필터로 전달 (인증이 실패해도 다음 필터로 넘겨 시큐리티가 처리하게 함)
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 토큰 정보를 꺼내온다.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
