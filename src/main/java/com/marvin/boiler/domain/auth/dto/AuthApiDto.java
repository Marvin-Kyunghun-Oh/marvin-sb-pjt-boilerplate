package com.marvin.boiler.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 인증(Auth) 관련 API 요청/응답 규격을 관리하는 DTO
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "인증 관련 API DTO")
public class AuthApiDto {

    @Schema(description = "로그인 요청")
    public record LoginRequest(
            @Schema(description = "이메일", example = "abc@naver.com")
            @NotBlank(message = "{validation.email.not_blank}")
            @Email(message = "{validation.email.format}")
            String email,

            @Schema(description = "비밀번호", example = "Password123!")
            @NotBlank(message = "{validation.password.not_blank}")
            String password
    ) {}

    @Schema(description = "토큰 응답")
    public record TokenResponse(
            @Schema(description = "토큰 타입", example = "Bearer")
            String grantType,

            @Schema(description = "액세스 토큰")
            String accessToken,

            @Schema(description = "리프레시 토큰")
            String refreshToken,

            @Schema(description = "액세스 토큰 만료 시간 (ms)", example = "3600000")
            Long accessTokenExpiresIn
    ) {}
}

