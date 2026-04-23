package com.marvin.boiler.domain.account.dto;

import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원(Account) 관련 API 요청/응답 규격을 관리하는 DTO
 * <p>
 * - 모든 API 관련 DTO를 이너 클래스(record)로 관리하여 파일 지옥을 방지합니다.
 * - 2단계 구조를 유지하여 가독성과 재사용성을 높입니다.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountApiDto {



    /**
     * 회원 목록 조회 응답 (GET /accounts)
     * 페이징 정보가 추가될 것을 대비하여 Wrapper 객체로 구성합니다.
     */
    @Schema(description = "회원 목록 페이징 응답")
    public record ListResponse(
            PageResponse<Summary> page
    ) {}

    /**
     * 목록 내 개별 회원 요약 정보
     */
    @Schema(description = "회원 요약 정보")
    public record Summary(
            @Schema(description = "계정 ID", example = "1")
            Long accountId,
            @Schema(description = "이름", example = "홍길동")
            String name,
            @Schema(description = "이메일", example = "hong@example.com")
            String email,
            @Schema(description = "상태 (ACTIVE: 활성, SUSPENDED: 중지, DELETED: 삭제)")
            Status status,
            @Schema(description = "생성 일시")
            LocalDateTime createdAt
    ) {}

    /**
     * 회원 단건 상세 조회 응답 (GET /accounts/{id})
     */
    @Schema(description = "회원 상세 정보 응답")
    public record GetResponse(
            @Schema(description = "계정 ID", example = "1")
            Long accountId,
            @Schema(description = "이름", example = "홍길동")
            String name,
            @Schema(description = "이메일", example = "hong@example.com")
            String email,
            @Schema(description = "상태 (ACTIVE: 활성, SUSPENDED: 중지, DELETED: 삭제)")
            Status status,
            @Schema(description = "VIP 여부", example = "false")
            Boolean vipYn,
            @Schema(description = "생성 일시")
            LocalDateTime createdAt,
            @Schema(description = "최종 수정 일시")
            LocalDateTime updatedAt
    ) {}

    /**
     * 회원 등록 요청 (POST /accounts)
     */
    @Schema(description = "회원 등록 요청")
    public record CreateRequest(
            @Schema(description = "이름", example = "홍길동")
            @NotBlank
            String name,
            @Schema(description = "이메일", example = "hong@example.com")
            @NotBlank @Email
            String email,
            @Schema(description = "상태 (ACTIVE: 활성, SUSPENDED: 중지, DELETED: 삭제)")
            @NotNull
            Status status
    ) {}

    /**
     * 회원 등록 응답 (POST /accounts)
     */
    @Schema(description = "회원 등록 응답")
    public record CreateResponse(
            @Schema(description = "생성된 계정 ID", example = "1")
            Long accountId
    ) {}

    /**
     * 회원 정보 수정 요청 (PATCH /accounts/{id})
     */
    @Schema(description = "회원 정보 수정 요청 (변경할 필드만 전송)")
    public record UpdateRequest(
            @Schema(description = "이름", example = "김철수")
            String name,
            @Schema(description = "이메일", example = "chul@example.com")
            @Email
            String email,
            @Schema(description = "상태 (ACTIVE: 활성, SUSPENDED: 중지, DELETED: 삭제)")
            Status status,
            @Schema(description = "VIP 여부", example = "true")
            Boolean vipYn
    ) {}
}
