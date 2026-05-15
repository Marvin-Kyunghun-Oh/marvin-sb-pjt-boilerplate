package com.marvin.boiler.global.security;

import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 보안 관련 유틸리티 클래스
 * - 서비스 레이어 등에서 현재 로그인한 사용자의 정보를 꺼낼 때 사용합니다.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    /**
     * 현재 로그인한 사용자의 ID(PK)를 반환합니다.
     * @return 사용자 ID (Long)
     * @throws BizException 인증 정보가 없을 경우 AUTH_UNAUTHORIZED 발생
     */
    public static Long getCurrentAccountId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            log.debug("SecurityContext에 인증 정보가 없습니다.");
            throw new BizException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            try {
                return Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                log.error("사용자 ID 형식이 올바르지 않습니다: {}", userDetails.getUsername());
                throw new BizException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        // 혹시 Principal이 String(ID) 자체일 경우를 대비한 폴백
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new BizException(ErrorCode.AUTH_UNAUTHORIZED);
        }
    }
}
