package com.marvin.boiler.domain.account.service;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.Password;
import com.marvin.boiler.domain.account.code.Status;
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
        Long accountId = 1L;
        String oldPassword = "Password123!";
        String newPassword = "Password456@";
        AccountApiDto.ChangePasswordRequest request =
                new AccountApiDto.ChangePasswordRequest(oldPassword, newPassword);

        Account account = createAccount(accountId, oldPassword);

        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));

        // when
        accountService.changePassword(accountId, request);

        // then
        assertThat(account.getPassword().getValue()).isEqualTo(newPassword);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 현재 비밀번호(oldPassword)가 불일치함")
    void changePassword_fail_invalid_old_password() {
        // given
        Long accountId = 1L;
        String actualPasswordInDb = "Password123!";
        String wrongOldPasswordInput = "WrongPass123*";
        AccountApiDto.ChangePasswordRequest request =
                new AccountApiDto.ChangePasswordRequest(wrongOldPasswordInput, "Password456&");

        Account account = createAccount(accountId, actualPasswordInDb);

        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));

        // when
        Throwable throwable = catchThrowable(() -> accountService.changePassword(accountId, request));

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
        Long accountId = 1L;
        String password = "Password123!";
        AccountApiDto.ChangePasswordRequest request =
                new AccountApiDto.ChangePasswordRequest(password, password);

        Account account = createAccount(accountId, password);

        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));

        // when
        Throwable throwable = catchThrowable(() -> accountService.changePassword(accountId, request));

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
        Long accountId = 1L;
        String oldPassword = "Password123!";
        String invalidNewPassword = "short";
        AccountApiDto.ChangePasswordRequest request =
                new AccountApiDto.ChangePasswordRequest(oldPassword, invalidNewPassword);

        Account account = createAccount(accountId, oldPassword);

        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));

        // when
        Throwable throwable = catchThrowable(() -> accountService.changePassword(accountId, request));

        // then
        assertThat(throwable)
                .isInstanceOf(BizException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCOUNT_INVALID_NEWPASSWORD_PATTERN);

        verify(accountRepository, times(1)).findById(accountId);
    }

    private Account createAccount(Long id, String rawPassword) {
        return Account.builder()
                .accountId(id)
                .email("abc@naver.com")
                .name("테스트")
                .password(Password.of(rawPassword))
                .status(Status.ACTIVE)
                .vipYn(false)
                .build();
    }
}
