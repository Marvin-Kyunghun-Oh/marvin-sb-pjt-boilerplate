package com.marvin.boiler.domain.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.service.AccountService;
import com.marvin.boiler.global.exception.ErrorCode;
import com.marvin.boiler.global.security.CustomAccessDeniedHandler;
import com.marvin.boiler.global.security.CustomAuthenticationEntryPoint;
import com.marvin.boiler.global.security.TokenProvider;
import com.marvin.boiler.config.WebMvcTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestConfig(AccountController.class)
@DisplayName("AccountController 테스트")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc; //  API 호출 시뮬레이션

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    // 외부 의존성을 차단 (ApplicationContext을 Mock으로 교체)
    @MockitoBean
    private AccountService accountService;


    private static final String BASE_URL = "/accounts";
    private static final ErrorCode BASE_ERROR = ErrorCode.REQUEST_ARGUMENT_NOT_VALID;

    @Nested
    @DisplayName("비밀번호 변경")
    class ChangePassword {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            Long accountId = 1L;
            String newPassword = "NewPass123!";
            var request = new AccountApiDto.ChangePasswordRequest(
                    "Password123!", newPassword, newPassword);

            // when & then
            mockMvc.perform(patch(BASE_URL + "/{accountId}/password", accountId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 : 비밀번호 규약 위반")
        void fail_invalid_password_pattern() throws Exception {

            // given
            Long accountId = 1L;
            String invalidNewPassword = "password";
            var request = new AccountApiDto.ChangePasswordRequest(
                    "Password123!", invalidNewPassword, invalidNewPassword);

            // when & then
            mockMvc.perform(patch(BASE_URL + "/{accountId}/password", accountId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value(BASE_ERROR.getCode()))
                    .andExpect(jsonPath("$.error.traceId").isNotEmpty())
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 : 새 비밀번호와 확인용 비밀번호가 불일치.")
        void fail_new_password_mismatch() throws Exception {

            // given
            Long accountId = 1L;
            String newPassword = "NewPass123!";
            String confirmNewPassword = "DifferentPass123!";
            var request = new AccountApiDto.ChangePasswordRequest(
                    "Password123!", newPassword, confirmNewPassword);

            // when & then
            mockMvc.perform(patch(BASE_URL + "/{accountId}/password", accountId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value(BASE_ERROR.getCode()))
                    .andExpect(jsonPath("$.error.traceId").isNotEmpty())
                    .andDo(print());
        }
    }


}
