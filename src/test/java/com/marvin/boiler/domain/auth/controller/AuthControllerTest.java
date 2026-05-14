package com.marvin.boiler.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvin.boiler.config.WebMvcTestConfig;
import com.marvin.boiler.domain.auth.dto.AuthApiDto;
import com.marvin.boiler.domain.auth.service.AuthService;
import com.marvin.boiler.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTestConfig(AuthController.class)
@DisplayName("AuthController 테스트")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private static final String BASE_URL = "/auth";
    private static final ErrorCode BASE_ERROR = ErrorCode.REQUEST_ARGUMENT_NOT_VALID;

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // [Given]
            AuthApiDto.LoginRequest request = new AuthApiDto.LoginRequest("user1@gmail.com", "Password123!");

            AuthApiDto.TokenResponse response = new AuthApiDto.TokenResponse(
                    "Bearer", "access-token", "refresh-token", 3600L
            );
            given(authService.login(any())).willReturn(response);

            // [When ~ Then]
            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.grantType").value("Bearer"))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                    .andDo(print());

            verify(authService, times(1)).login(any());
        }

        @Test
        @DisplayName("실패: 이메일 형식이 올바르지 않음")
        void fail_invalid_email() throws Exception {
            // [Given]
            AuthApiDto.LoginRequest request = new AuthApiDto.LoginRequest("invalid-email", "Password123!");

            // [When ~ Then]
            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value(BASE_ERROR.getCode()))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패: 비밀번호 누락")
        void fail_blank_password() throws Exception {
            // [Given]
            AuthApiDto.LoginRequest request = new AuthApiDto.LoginRequest("user1@gmail.com", "");

            // [When ~ Then]
            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value(BASE_ERROR.getCode()))
                    .andDo(print());
        }
    }
}
