package com.marvin.boiler.domain.account.dto;

import com.marvin.boiler.domain.account.Password;
import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원(Account) 관련 API 요청/응답 규격을 관리하는 DTO
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "회원 관련 API DTO")
public class AccountApiDto {

    @Schema(description = "회원 목록 조회 응답")
    public record ListResponse(
            @Schema(description = "페이징 결과")
            PageResponse<Summary> page
    ) {}

    @Schema(description = "회원 요약 정보")
    public record Summary(
            @Schema(description = "계정 ID", example = "1")
            Long accountId,
            @Schema(description = "이름", example = "마빈")
            String name,
            @Schema(description = "이메일", example = "marvin@example.com")
            String email,
            @Schema(description = "상태")
            Status status,
            @Schema(description = "생성일시")
            LocalDateTime createdAt
    ) {}

    @Schema(description = "회원 상세 정보 응답")
    public record GetResponse(
            @Schema(description = "계정 ID", example = "1")
            Long accountId,
            @Schema(description = "이름", example = "마빈")
            String name,
            @Schema(description = "이메일", example = "marvin@example.com")
            String email,
            @Schema(description = "상태")
            Status status,
            @Schema(description = "VIP 여부", example = "false")
            Boolean vipYn,
            @Schema(description = "생성일시")
            LocalDateTime createdAt,
            @Schema(description = "수정일시")
            LocalDateTime updatedAt
    ) {}

    @Schema(description = "회원 가입 요청")
    public record CreateRequest(
            @Schema(description = "이름", example = "마빈")
            @NotBlank(message = "{validation.name.not_blank}")
            String name,
            @Schema(description = "이메일", example = "marvin@example.com")
            @NotBlank(message = "{validation.email.not_blank}")
            @Email(message = "{validation.email.format}")
            String email,
            @Schema(description = "상태 (ACTIVE, INACTIVE 등)")
            @NotNull(message = "{validation.status.not_null}")
            Status status,
            @Schema(description = "비밀번호", example = "password123")
            @NotBlank(message = "{validation.password.not_blank}")
            @Pattern(
                    regexp = Password.REGEX,
                    message = "{validation.password.pattern}"
            )
            String password,
            @Schema(description = "비밀번호_확인", example = "password123")
            @NotBlank(message = "{validation.password.not_blank}")
            String confirmPassword

    ) {

        @AssertTrue(message = "{validation.password.mismatch}")
        public boolean isPasswordMatched() {
            return password != null && password.equals(confirmPassword);
        }
    }

    @Schema(description = "회원 가입 응답")
    public record CreateResponse(
            @Schema(description = "생성된 계정 ID", example = "1")
            Long accountId
    ) {}

    @Schema(description = "회원 정보 수정 요청")
    public record UpdateRequest(
            @Schema(description = "이름", example = "마빈 수정")
            String name,
            @Schema(description = "이메일", example = "updated@example.com")
            @Email(message = "{validation.email.format}")
            String email,
            @Schema(description = "상태")
            Status status,
            @Schema(description = "VIP 여부", example = "true")
            Boolean vipYn
    ) {}

    @Schema(description = "비밀번호 변경")
    public record ChangePasswordRequest(
            @Schema(description = "이전 비밀번호", example = "password123")
            @NotBlank(message = "{validation.password.not_blank}")
            @Pattern(
                    regexp = Password.REGEX,
                    message = "{validation.password.pattern}"
            )
            String oldPassword,

            @Schema(description = "변경 비밀번호", example = "Change1234!")
            @NotBlank(message = "{validation.password.not_blank}")
            @Pattern(
                    regexp = Password.REGEX,
                    message = "{validation.password.pattern}"
            )
            String newPassword
    ){}
}
