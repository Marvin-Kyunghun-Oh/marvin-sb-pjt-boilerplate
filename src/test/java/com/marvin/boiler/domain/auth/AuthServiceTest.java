package com.marvin.boiler.domain.auth;


import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.AccountFixture;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import com.marvin.boiler.domain.auth.dto.AuthApiDto;
import com.marvin.boiler.domain.auth.service.AuthService;
import com.marvin.boiler.global.security.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.marvin.boiler.domain.account.AccountFixture.DEFAULT_EMAIL;
import static com.marvin.boiler.domain.account.AccountFixture.DEFAULT_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
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


    @Nested
    @DisplayName("로그인")
    class login {

        @Test
        @DisplayName("성공")
        void success() {

            // [Given]
            String loginEmail = DEFAULT_EMAIL;
            String loginPassword = DEFAULT_PASSWORD;
            AuthApiDto.LoginRequest request =
                    new AuthApiDto.LoginRequest(loginEmail, loginPassword);
            Account account = AccountFixture.createAccount();

            // 1. 이메일로 회원 검색
            // (Service 구현 시 account.getStatus() == Status.ACTIVE 인지 확인하는 로직이 필요함을 암시)
            given(accountRepository.findByEmail(loginEmail))
                    .willReturn(Optional.of(account));

            // 2. 비밀번호 체크
            // (anyString() 대신 account.getPassword().getValue()를 사용하여 실제 대상과 비교하는지 정밀 검증)
            given(passwordEncoder.matches(eq(loginPassword), eq(account.getPassword().getValue())))
                    .willReturn(true);

            // 3. 토큰 발급 Mock 설정
            // (TokenProvider가 Authentication 객체를 받아 토큰을 생성하므로 이에 대한 Mocking이 필요함)
            String mockAccessToken = "mock-access-token";
            String mockRefreshToken = "mock-refresh-token";
            given(tokenProvider.createAccessToken(any(Authentication.class)))
                    .willReturn(mockAccessToken);
            given(tokenProvider.createRefreshToken(any(Authentication.class)))
                    .willReturn(mockRefreshToken);


            // [When]
            // 모든 인증 결과의 공통 응답 규격인 TokenResponse를 사용
            AuthApiDto.TokenResponse response = authService.login(request);


            // [Then]
            assertThat(response.accessToken()).isEqualTo(mockAccessToken);
            assertThat(response.refreshToken()).isEqualTo(mockRefreshToken);
            assertThat(response.grantType()).isEqualTo("Bearer");

            verify(accountRepository, times(1)).findByEmail(loginEmail);
            verify(passwordEncoder, times(1)).matches(eq(loginPassword), eq(account.getPassword().getValue()));
            verify(tokenProvider, times(1)).createAccessToken(any(Authentication.class));
            verify(tokenProvider, times(1)).createRefreshToken(any(Authentication.class));
        }
    }

}
