package com.marvin.boiler.domain.demo.controller;

import com.marvin.boiler.config.WebMvcTestConfig;
import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.service.AccountService;
import com.marvin.boiler.global.exception.ErrorCode;
import com.marvin.boiler.global.security.CustomAccessDeniedHandler;
import com.marvin.boiler.global.security.CustomAuthenticationEntryPoint;
import com.marvin.boiler.global.security.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestConfig(SecurityDemoController.class)
@Import({CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
@DisplayName("SecurityDemoController 보안 검증 테스트")
class SecurityDemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private AccountService accountService;

    private static final String VALID_TOKEN = "Bearer valid.token.here";
    private static final String INVALID_TOKEN = "Bearer invalid.token.here";

    @Nested
    @DisplayName("인증(Authentication) 테스트 - 401 Unauthorized")
    class AuthenticationTest {

        @Test
        @DisplayName("성공: 유효한 토큰으로 접근 시 200 OK")
        void auth_success() throws Exception {
            // given
            given(tokenProvider.validateToken(anyString())).willReturn(true);
            given(tokenProvider.getAuthentication(anyString())).willReturn(
                    new UsernamePasswordAuthenticationToken(
                            new User("user@test.com", "", List.of(new SimpleGrantedAuthority("ROLE_USER"))),
                            "",
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    )
            );

            // when & then
            mockMvc.perform(get("/demo/auth")
                            .header("Authorization", VALID_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value("인증 성공: 유효한 토큰을 보유하고 있습니다."));
        }

        @Test
        @DisplayName("실패: 토큰 없이 접근 시 401 Unauthorized (AUTH_UNAUTHORIZED)")
        void auth_fail_no_token() throws Exception {
            mockMvc.perform(get("/demo/auth"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.AUTH_UNAUTHORIZED.getCode()));
        }

        @Test
        @DisplayName("실패: 변조된 토큰으로 접근 시 401 Unauthorized (AUTH_TOKEN_INVALID)")
        void auth_fail_invalid_token() throws Exception {
            // given
            given(tokenProvider.validateToken(anyString())).willThrow(new MalformedJwtException("Invalid token"));

            // when & then
            mockMvc.perform(get("/demo/auth")
                            .header("Authorization", INVALID_TOKEN))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.AUTH_TOKEN_INVALID.getCode()));
        }

        @Test
        @DisplayName("실패: 만료된 토큰으로 접근 시 401 Unauthorized (AUTH_TOKEN_EXPIRED)")
        void auth_fail_expired_token() throws Exception {
            // given
            given(tokenProvider.validateToken(anyString())).willThrow(new ExpiredJwtException(null, null, "Token expired"));

            // when & then
            mockMvc.perform(get("/demo/auth")
                            .header("Authorization", VALID_TOKEN))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.AUTH_TOKEN_EXPIRED.getCode()));
        }
    }

    @Nested
    @DisplayName("인가(Authorization) 테스트 - 403 Forbidden")
    class AuthorizationTest {

        @Test
        @DisplayName("성공: VIP 권한을 가진 사용자가 VIP 전용 API 접근 시 200 OK")
        void vip_success() throws Exception {
            // given
            given(tokenProvider.validateToken(anyString())).willReturn(true);
            given(tokenProvider.getAuthentication(anyString())).willReturn(
                    new UsernamePasswordAuthenticationToken(
                            new User("vip@test.com", "", List.of(new SimpleGrantedAuthority("ROLE_VIP"))),
                            "",
                            List.of(new SimpleGrantedAuthority("ROLE_VIP"))
                    )
            );

            // when & then
            mockMvc.perform(get("/demo/vip")
                            .header("Authorization", VALID_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value("인가 성공: VIP 등급 혜택을 이용하실 수 있습니다."));
        }

        @Test
        @DisplayName("실패: 일반 권한(ROLE_USER) 사용자가 VIP 전용 API 접근 시 403 Forbidden (AUTH_FORBIDDEN)")
        void vip_fail_forbidden() throws Exception {
            // given
            given(tokenProvider.validateToken(anyString())).willReturn(true);
            given(tokenProvider.getAuthentication(anyString())).willReturn(
                    new UsernamePasswordAuthenticationToken(
                            new User("user@test.com", "", List.of(new SimpleGrantedAuthority("ROLE_USER"))),
                            "",
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    )
            );

            // when & then
            mockMvc.perform(get("/demo/vip")
                            .header("Authorization", VALID_TOKEN))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error.code").value(ErrorCode.AUTH_FORBIDDEN.getCode()));
        }
    }

    @Nested
    @DisplayName("인증 정보 주입 테스트 - @AuthenticationPrincipal -> @CurrentUser")
    class CurrentUserTest {

        @Test
        @DisplayName("성공: 유효한 토큰으로 요청 시 현재 로그인한 사용자의 ID를 반환한다")
        void get_me_success() throws Exception {
            // given
            String accountId = "1";
            given(tokenProvider.validateToken(anyString())).willReturn(true);
            given(tokenProvider.getAuthentication(anyString())).willReturn(
                    new UsernamePasswordAuthenticationToken(
                            new User(accountId, "", List.of(new SimpleGrantedAuthority("ROLE_USER"))),
                            "",
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    )
            );

            // when & then
            mockMvc.perform(get("/demo/me")
                            .header("Authorization", VALID_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(Long.valueOf(accountId)));
        }

        @Test
        @DisplayName("성공: /demo/my-info 호출 시 현재 사용자의 상세 정보를 반환한다")
        void get_my_info_success() throws Exception {
            // given
            Long accountId = 1L;
            AccountApiDto.GetResponse response = new AccountApiDto.GetResponse(
                    accountId, "마빈", "marvin@test.com", Status.ACTIVE, false, LocalDateTime.now(), LocalDateTime.now()
            );

            given(tokenProvider.validateToken(anyString())).willReturn(true);
            given(tokenProvider.getAuthentication(anyString())).willReturn(
                    new UsernamePasswordAuthenticationToken(
                            new User(accountId.toString(), "", List.of(new SimpleGrantedAuthority("ROLE_USER"))),
                            "",
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    )
            );
            given(accountService.getAccount(anyLong())).willReturn(response);

            // when & then
            mockMvc.perform(get("/demo/my-info")
                            .header("Authorization", VALID_TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accountId").value(accountId))
                    .andExpect(jsonPath("$.data.email").value("marvin@test.com"));
        }
    }
}
