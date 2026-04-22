package com.marvin.boiler.domain.account.dto;

import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.global.dto.PageResponse;
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
    public record ListResponse(
            PageResponse<Summary> page
    ) {}

    /**
     * 목록 내 개별 회원 요약 정보
     */
    public record Summary(
            Long accountId,
            String name,
            String email,
            Status status,
            LocalDateTime createdAt
    ) {}

    /**
     * 회원 단건 상세 조회 응답 (GET /accounts/{id})
     */
    public record GetResponse(
            Long accountId,
            String name,
            String email,
            Status status,
            Boolean vipYn,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    /**
     * 회원 등록 요청 (POST /accounts)
     */
    public record CreateRequest(
            @NotBlank
            String name,
            @NotBlank @Email
            String email,
            @NotNull
            Status status
    ) {}

    /**
     * 회원 등록 응답 (POST /accounts)
     */
    public record CreateResponse(
            Long accountId
    ) {}

    /**
     * 회원 정보 수정 요청 (PATCH /accounts/{id})
     */
    public record UpdateRequest(
            String name,
            @Email
            String email,
            Status status,
            Boolean vipYn
    ) {}
}
