package com.marvin.boiler.domain.account.service;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.Password;
import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

        Account account = Account.builder()
                .accountId(accountId)
                .email("abc@naver.com")
                .name("테스트")
                .password(Password.of(oldPassword))
                .status(Status.ACTIVE)
                .vipYn(false)
                .build();

        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(account));

        // when
        accountService.changePassword(accountId, request);

        // then
        assertEquals(newPassword, account.getPassword().getValue());
    }
}
