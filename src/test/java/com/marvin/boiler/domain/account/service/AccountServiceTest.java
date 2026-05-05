package com.marvin.boiler.domain.account.service;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.AccountFixture;
import com.marvin.boiler.domain.account.Password;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void changePassword_success() {
        // given
        Long accountId = ACCOUNTID_1;
        String oldPassword = DEFAULT_PASSWORD; // 상수 활용
        String newPassword = "Password456@"; // 테스트 타겟 값은 리터럴로 명시
        AccountApiDto.ChangePasswordRequest request =
                new AccountApiDto.ChangePasswordRequest(oldPassword, newPassword);

        Account account = AccountFixture.createAccount(); // 기본 빌더 활용

        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));

        // when
        accountService.changePassword(accountId, request);

        // then
        assertThat(account.getPassword().getValue()).isEqualTo(newPassword);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 존재하지 않는 계정")
    void changePassword_fail_account_not_found() {
        // given
        Long invalidAccountId = 999L;
        AccountApiDto.ChangePasswordRequest request =
                new AccountApiDto.ChangePasswordRequest(DEFAULT_PASSWORD, "NewPass456@");

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
    @DisplayName("비밀번호 변경 - 실패: 현재 비밀번호(oldPassword)가 불일치함")
    void changePassword_fail_invalid_old_password() {
        // given
        Long accountId = ACCOUNTID_1;
        String actualPasswordInDb = DEFAULT_PASSWORD;
        String wrongOldPasswordInput = "WrongPass123*"; // 틀린 값임을 명시
        AccountApiDto.ChangePasswordRequest request =
                new AccountApiDto.ChangePasswordRequest(wrongOldPasswordInput, "Password456&");

        Account account = AccountFixture.createAccountBuilder()
                .password(Password.of(actualPasswordInDb))
                .build();

        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));

        // when
        Throwable throwable = catchThrowable(() ->
                accountService.changePassword(accountId, request));

        // then
        assertThat(throwable)
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCOUNT_INVALID_PASSWORD);
        
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 새 비밀번호가 기존 비밀번호와 같음")
    void changePassword_fail_same_as_old_password() {
        // given
        Long accountId = ACCOUNTID_1;
        String password = DEFAULT_PASSWORD;
        AccountApiDto.ChangePasswordRequest request =
                new AccountApiDto.ChangePasswordRequest(password, password);

        Account account = AccountFixture.createAccountBuilder()
                .password(Password.of(password))
                .build();

        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));

        // when
        Throwable throwable = catchThrowable(() ->
                accountService.changePassword(accountId, request));

        // then
        assertThat(throwable)
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCOUNT_SAME_AS_OLD_PASSWORD);

        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 새 비밀번호가 규약을 지키지 않음")
    void changePassword_fail_invalid_newPassword_pattern() {
        // given
        Long accountId = ACCOUNTID_1;
        String oldPassword = DEFAULT_PASSWORD;
        String invalidNewPassword = "short"; // 규약 위반임을 명시
        AccountApiDto.ChangePasswordRequest request =
                new AccountApiDto.ChangePasswordRequest(oldPassword, invalidNewPassword);

        Account account = AccountFixture.createAccount();

        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));

        // when
        Throwable throwable = catchThrowable(() ->
                accountService.changePassword(accountId, request));

        // then
        assertThat(throwable)
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCOUNT_INVALID_NEWPASSWORD_PATTERN);

        verify(accountRepository, times(1)).findById(accountId);
    }

}
