package com.marvin.boiler.domain.auth.controller;


import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.auth.dto.AuthApiDto;
import com.marvin.boiler.domain.auth.service.AuthService;
import com.marvin.boiler.global.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "로그인 및 권한관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "로그인을 통해 토큰을 발급받습니다.")
    @PostMapping("/login")
    public BaseResponse<AuthApiDto.TokenResponse> login(@Valid @RequestBody AuthApiDto.LoginRequest request) {
        return BaseResponse.ok(authService.login(request), "common.success");
    }

}
