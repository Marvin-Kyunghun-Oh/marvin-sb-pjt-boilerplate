package com.marvin.boiler.domain.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import com.marvin.boiler.global.test.IntegrationTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.marvin.boiler.domain.account.AccountFixture.DEFAULT_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTestConfig
@DisplayName("Account 통합 테스트")
class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("비밀번호 변경 흐름")
    class ChangePasswordFlow {

        @Test
        @DisplayName("성공: 모든 레이어를 거쳐 실제 DB의 비밀번호가 변경된다")
        void changePassword_full_flow_success() throws Exception {
            // 1. Given: 실제 DB에 회원 저장 (암호화된 비밀번호로 저장해야 matches를 통과함)
            Account account = AccountFixture.createAccountBuilder()
                    .accountId(null)
                    .password(Password.fromEncoded(passwordEncoder.encode(DEFAULT_PASSWORD)))
                    .build();
            Account savedAccount = accountRepository.saveAndFlush(account);
            Long accountId = savedAccount.getAccountId();

            String newPassword = "NewPassword123!";
            AccountApiDto.ChangePasswordRequest request = new AccountApiDto.ChangePasswordRequest(
                    DEFAULT_PASSWORD, newPassword, newPassword);

            // 2. When: API 호출
            mockMvc.perform(patch("/accounts/{accountId}/password", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(print());

            // 3. Then: 실제 DB를 다시 조회하여 값이 변경되었는지 최종 확인
            // 영속성 컨텍스트 초기화 효과를 위해 다시 조회
            Account updatedAccount = accountRepository.findById(accountId).orElseThrow();
            assertThat(passwordEncoder.matches(newPassword, updatedAccount.getPassword().getValue())).isTrue();
        }

        @Test
        @DisplayName("실패: 필드 매칭 검증기(@FieldMatch)가 작동하여 불일치 시 400 에러를 반환한다")
        void changePassword_fail_validation() throws Exception {
            // given
            Long accountId = 1L; // 존재 여부와 상관없이 DTO 검증에서 먼저 걸림
            AccountApiDto.ChangePasswordRequest request = new AccountApiDto.ChangePasswordRequest(
                    DEFAULT_PASSWORD, "NewPass123!", "MismatchPass123!");

            // when & then
            mockMvc.perform(patch("/accounts/{accountId}/password", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andDo(print());
        }
    }
}
