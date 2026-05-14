package com.marvin.boiler.domain.auth.service;


import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.AccountFixture;
import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import com.marvin.boiler.domain.auth.dto.AuthApiDto;
import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import com.marvin.boiler.global.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.marvin.boiler.domain.account.AccountFixture.DEFAULT_EMAIL;
import static com.marvin.boiler.domain.account.AccountFixture.DEFAULT_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    private AuthApiDto.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new AuthApiDto.LoginRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }


    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("성공")
        void success() {

            // [Given]
            Account account = AccountFixture.createAccount();

            // 1. 이메일로 회원 검색
            given(accountRepository.findByEmail(DEFAULT_EMAIL))
                    .willReturn(Optional.of(account));

            // 2. 비밀번호 체크
            given(passwordEncoder.matches(eq(DEFAULT_PASSWORD), eq(account.getPassword().getValue())))
                    .willReturn(true);

            // 3. 토큰 발급 Mock 설정
            String mockAccessToken = "mock-access-token";
            String mockRefreshToken = "mock-refresh-token";
            given(tokenProvider.createAccessToken(any(Authentication.class)))
                    .willReturn(mockAccessToken);
            given(tokenProvider.createRefreshToken(any(Authentication.class)))
                    .willReturn(mockRefreshToken);


            // [When]
            AuthApiDto.TokenResponse response = authService.login(loginRequest);


            // [Then]
            assertThat(response.accessToken()).isEqualTo(mockAccessToken);
            assertThat(response.refreshToken()).isEqualTo(mockRefreshToken);
            assertThat(response.grantType()).isEqualTo(TokenProvider.BEARER_TYPE);

            verify(accountRepository, times(1)).findByEmail(DEFAULT_EMAIL);
            verify(passwordEncoder, times(1)).matches(eq(DEFAULT_PASSWORD), eq(account.getPassword().getValue()));

            // ArgumentCaptor를 사용하여 Principal 정밀 검증
            ArgumentCaptor<Authentication> authCaptor = ArgumentCaptor.forClass(Authentication.class);
            verify(tokenProvider, times(1)).createAccessToken(authCaptor.capture());
            verify(tokenProvider, times(1)).createRefreshToken(any(Authentication.class));

            assertThat(authCaptor.getValue().getPrincipal()).isEqualTo(account.getAccountId().toString());
        }


        @Test
        @DisplayName("실패: 로그인 이메일로 회원을 찾을 수 없음.")
        void not_found_account() {
            // [Given]
            String wrongEmail = "wrong@naver.com";
            AuthApiDto.LoginRequest wrongRequest = new AuthApiDto.LoginRequest(wrongEmail, DEFAULT_PASSWORD);

            given(accountRepository.findByEmail(wrongEmail))
                    .willReturn(Optional.empty());


            // [When]
            Throwable throwable = catchThrowable(() -> authService.login(wrongRequest));


            // [Then]
            assertThat(throwable)
                    .isInstanceOf(BizException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.AUTH_LOGIN_FAILED);

            verify(accountRepository, times(1)).findByEmail(wrongEmail);
        }


        @Test
        @DisplayName("실패: 비밀번호 불일치")
        void password_invalid() {
            // [Given]
            String wrongPassword = "wrongPass123!";
            AuthApiDto.LoginRequest wrongRequest = new AuthApiDto.LoginRequest(DEFAULT_EMAIL, wrongPassword);

            Account account = AccountFixture.createAccount();

            given(accountRepository.findByEmail(DEFAULT_EMAIL))
                    .willReturn(Optional.of(account));
            given(passwordEncoder.matches(eq(wrongPassword), eq(account.getPassword().getValue())))
                    .willReturn(false);

            // [When]
            Throwable throwable = catchThrowable(() -> authService.login(wrongRequest));


            // [Then]
            assertThat(throwable)
                    .isInstanceOf(BizException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.AUTH_LOGIN_FAILED);

            verify(accountRepository, times(1)).findByEmail(DEFAULT_EMAIL);
            verify(passwordEncoder, times(1)).matches(eq(wrongPassword), eq(account.getPassword().getValue()));
        }


        @Test
        @DisplayName("실패: 회원상태가 '활성'이 아님.")
        void invalid_status() {
            // [Given]
            // 리팩토링된 Fixture 메서드 사용
            Account account = AccountFixture.createSuspendedAccount();

            given(accountRepository.findByEmail(DEFAULT_EMAIL))
                    .willReturn(Optional.of(account));
            given(passwordEncoder.matches(eq(DEFAULT_PASSWORD), eq(account.getPassword().getValue())))
                    .willReturn(true);

            // [When]
            Throwable throwable = catchThrowable(() -> authService.login(loginRequest));


            // [Then]
            assertThat(throwable)
                    .isInstanceOf(BizException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.AUTH_LOGIN_INVALID_STATUS);

            verify(accountRepository, times(1)).findByEmail(DEFAULT_EMAIL);
            verify(passwordEncoder, times(1)).matches(eq(DEFAULT_PASSWORD), eq(account.getPassword().getValue()));
        }
    }



}
