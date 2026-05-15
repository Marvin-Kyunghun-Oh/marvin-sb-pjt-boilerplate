package com.marvin.boiler.domain.demo.controller;

import com.marvin.boiler.global.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 보안(인증/인가) 기능 검증을 위한 데모 컨트롤러
 * - /demo/** 경로는 SecurityConfig에서 permitAll()에 등록되지 않았으므로 인증이 필수입니다.
 */
@Tag(name = "Security Demo", description = "보안 기능 검증용 API")
@Slf4j
@RestController
@RequestMapping("/demo")
public class SecurityDemoController {

    @Operation(summary = "인증 테스트 API", description = "유효한 토큰이 있어야 접근 가능합니다. (401 테스트용)")
    @GetMapping("/auth")
    public BaseResponse<String> authTest() {
        return BaseResponse.ok("인증 성공: 유효한 토큰을 보유하고 있습니다.");
    }

    @Operation(summary = "VIP 인가 테스트 API", description = "ROLE_VIP 권한이 있어야 접근 가능합니다. (403 테스트용)")
    @GetMapping("/vip")
    @PreAuthorize("hasRole('VIP')")
    public BaseResponse<String> vipTest() {
        return BaseResponse.ok("인가 성공: VIP 등급 혜택을 이용하실 수 있습니다.");
    }
}
