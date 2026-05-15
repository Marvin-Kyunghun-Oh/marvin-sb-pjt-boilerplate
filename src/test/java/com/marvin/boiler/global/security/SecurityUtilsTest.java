package com.marvin.boiler.global.security;

import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SecurityUtils 단위 테스트")
class SecurityUtilsTest {

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("성공: SecurityContext에 인증 정보가 있으면 사용자 ID(Long)를 반환한다")
    void getCurrentAccountId_success() {
        // given
        String accountId = "123";
        User principal = new User(accountId, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        Long result = SecurityUtils.getCurrentAccountId();

        // then
        assertThat(result).isEqualTo(123L);
    }

    @Test
    @DisplayName("실패: 인증 정보가 없으면 AUTH_UNAUTHORIZED 예외가 발생한다")
    void getCurrentAccountId_fail_no_authentication() {
        // given
        SecurityContextHolder.clearContext();

        // when & then
        assertThatThrownBy(SecurityUtils::getCurrentAccountId)
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_UNAUTHORIZED);
    }

    @Test
    @DisplayName("실패: 익명 사용자(anonymousUser)인 경우 AUTH_UNAUTHORIZED 예외가 발생한다")
    void getCurrentAccountId_fail_anonymous() {
        // given
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken("anonymousUser", "", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when & then
        assertThatThrownBy(SecurityUtils::getCurrentAccountId)
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_UNAUTHORIZED);
    }
}
