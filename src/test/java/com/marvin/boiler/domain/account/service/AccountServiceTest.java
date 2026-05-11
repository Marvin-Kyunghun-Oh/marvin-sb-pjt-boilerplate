package com.marvin.boiler.domain.account.service;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.AccountFixture;
import com.marvin.boiler.domain.account.Password;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.marvin.boiler.domain.account.AccountFixture.ACCOUNTID_1;
import static com.marvin.boiler.domain.account.AccountFixture.DEFAULT_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService 테스트")
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;



    @Nested
    @DisplayName("비밀번호 변경")
    class changePassword {

        @Test
        @DisplayName("성공")
        void success() {
            // given
            Long accountId = ACCOUNTID_1;
            String oldPassword = DEFAULT_PASSWORD; // 상수 활용
            String newPassword = "Password456@"; // 테스트 타겟 값은 리터럴로 명시
            String encodeNewPassword = "encodePassword456@"; // Mock 시나리오:이렇게 인코드됐다고 친다.
            AccountApiDto.ChangePasswordRequest request =
                    new AccountApiDto.ChangePasswordRequest(oldPassword, newPassword, newPassword);

            Account account = AccountFixture.createAccount(); // 기본 빌더 활용

            given(accountRepository.findById(accountId))
                    .willReturn(Optional.of(account));

            // PasswordEncode ==> 이전 비밀번호 일치여부 체크
            given(passwordEncoder.matches(eq(oldPassword), anyString()))
                    .willReturn(true);
            // PasswordEncode ==> 새로운 비밀번호 암호화
            given(passwordEncoder.encode(newPassword))
                    .willReturn(encodeNewPassword);


            // when
            accountService.changePassword(accountId, request);

            // then
            assertThat(account.getPassword().getValue()).isEqualTo(encodeNewPassword);
            verify(passwordEncoder, times(1)).matches(eq(oldPassword), anyString());
            verify(passwordEncoder, times(1)).encode(newPassword);
            verify(accountRepository, times(1)).findById(accountId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 계정")
        void fail_account_not_found() {
            // given
            Long invalidAccountId = 999L;
            String newPassword = "NewPass456@";
            AccountApiDto.ChangePasswordRequest request =
                    new AccountApiDto.ChangePasswordRequest(DEFAULT_PASSWORD, newPassword, newPassword);

            given(accountRepository.findById(invalidAccountId))
                    .willReturn(Optional.empty());

            // when
            Throwable throwable = catchThrowable(() ->
                    accountService.changePassword(invalidAccountId, request));

            // then
            assertThat(throwable)
                    .isInstanceOf(BizException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.ACCOUNT_NOT_FOUND);

            verify(accountRepository, times(1)).findById(invalidAccountId);
            verify(accountRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 현재 비밀번호(oldPassword)가 불일치함")
        void fail_invalid_old_password() {
            // given
            Long accountId = ACCOUNTID_1;
            String actualPasswordInDb = DEFAULT_PASSWORD;
            String wrongOldPasswordInput = "WrongPass123*"; // 틀린 값임을 명시
            String newPassword = "Password456&";
            AccountApiDto.ChangePasswordRequest request =
                    new AccountApiDto.ChangePasswordRequest(wrongOldPasswordInput, newPassword, newPassword);

            Account account = AccountFixture.createAccountBuilder()
                    .password(Password.of(actualPasswordInDb))
                    .build();

            given(accountRepository.findById(accountId))
                    .willReturn(Optional.of(account));
            given(passwordEncoder.matches(eq(wrongOldPasswordInput), anyString()))
                    .willReturn(false);

            // when
            Throwable throwable = catchThrowable(() ->
                    accountService.changePassword(accountId, request));

            // then
            assertThat(throwable)
                    .isInstanceOf(BizException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.ACCOUNT_INVALID_PASSWORD);

            verify(accountRepository, times(1)).findById(accountId);
            verify(passwordEncoder, times(1)).matches(eq(wrongOldPasswordInput), anyString());
        }

        @Test
        @DisplayName("실패: 새 비밀번호가 기존 비밀번호와 같음")
        void fail_same_as_old_password() {
            // given
            Long accountId = ACCOUNTID_1;
            String password = DEFAULT_PASSWORD;
            AccountApiDto.ChangePasswordRequest request =
                    new AccountApiDto.ChangePasswordRequest(password, password, password);

            Account account = AccountFixture.createAccountBuilder()
                    .password(Password.of(password))
                    .build();

            given(accountRepository.findById(accountId))
                    .willReturn(Optional.of(account));
            given(passwordEncoder.matches(eq(password), anyString()))
                    .willReturn(true);

            // when
            Throwable throwable = catchThrowable(() ->
                    accountService.changePassword(accountId, request));

            // then
            assertThat(throwable)
                    .isInstanceOf(BizException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.ACCOUNT_SAME_AS_OLD_PASSWORD);

            verify(accountRepository, times(1)).findById(accountId);
            verify(passwordEncoder, times(2)).matches(eq(password), anyString());
        }

        @Test
        @DisplayName("실패: 새 비밀번호가 규약을 지키지 않음")
        void fail_invalid_newPassword_pattern() {
            // given
            Long accountId = ACCOUNTID_1;
            String oldPassword = DEFAULT_PASSWORD;
            String invalidNewPassword = "short"; // 규약 위반임을 명시
            AccountApiDto.ChangePasswordRequest request =
                    new AccountApiDto.ChangePasswordRequest(oldPassword, invalidNewPassword, invalidNewPassword);

            Account account = AccountFixture.createAccount();

            given(accountRepository.findById(accountId))
                    .willReturn(Optional.of(account));
            given(passwordEncoder.matches(eq(oldPassword), anyString()))
                    .willReturn(true);

            // when
            Throwable throwable = catchThrowable(() ->
                    accountService.changePassword(accountId, request));

            // then
            assertThat(throwable)
                    .isInstanceOf(BizException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.ACCOUNT_INVALID_NEWPASSWORD_PATTERN);

            verify(accountRepository, times(1)).findById(accountId);
            verify(passwordEncoder, times(1)).matches(eq(oldPassword), anyString());
        }

    }

}
