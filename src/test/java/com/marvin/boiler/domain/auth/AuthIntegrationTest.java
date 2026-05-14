package com.marvin.boiler.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvin.boiler.config.IntegrationTestConfig;
import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.AccountFixture;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import com.marvin.boiler.domain.auth.dto.AuthApiDto;
import com.marvin.boiler.global.security.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.marvin.boiler.domain.account.AccountFixture.DEFAULT_EMAIL;
import static com.marvin.boiler.domain.account.AccountFixture.DEFAULT_PASSWORD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTestConfig
@DisplayName("인증 통합 테스트")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("성공: 실제 DB 회원 정보를 바탕으로 로그인을 성공하고 토큰을 발급받는다")
    void login_full_flow_success() throws Exception {
        // [Given] 실제 DB에 암호화된 비밀번호를 가진 회원 저장
        Account account = AccountFixture.createEncodedAccountBuilder(passwordEncoder)
                .accountId(null) // IDENTITY 전략을 위해 null 설정
                .build();
        accountRepository.save(account);

        AuthApiDto.LoginRequest request = new AuthApiDto.LoginRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD);

        // [When & Then] 실제 API 호출 및 토큰 발급 확인
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.grantType").value(TokenProvider.BEARER_TYPE))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andDo(print());
    }
}
