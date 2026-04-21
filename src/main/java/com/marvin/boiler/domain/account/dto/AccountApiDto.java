package com.marvin.boiler.domain.account.dto;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.code.Status;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
            List<Summary> accounts
    ) {
        public static ListResponse from(List<Account> accounts) {
            return new ListResponse(
                    accounts.stream()
                            .map(Summary::from)
                            .toList()
            );
        }
    }

    /**
     * 목록 내 개별 회원 요약 정보
     */
    public record Summary(
            Long accountId,
            String name,
            String email,
            Status status,
            LocalDateTime createdAt
    ) {
        public static Summary from(Account account) {
            return new Summary(
                    account.getAccountId(),
                    account.getName(),
                    account.getEmail(),
                    account.getStatus(),
                    account.getCreatedAt()
            );
        }
    }

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
    ) {
        public static GetResponse from(Account account) {
            return new GetResponse(
                    account.getAccountId(),
                    account.getName(),
                    account.getEmail(),
                    account.getStatus(),
                    account.getVipYn(),
                    account.getCreatedAt(),
                    account.getUpdatedAt()
            );
        }
    }

    /**
     * 회원 등록 요청 (POST /accounts)
     */
    public record CreateRequest(
            String name,
            String email,
            Status status
    ) {
        public Account toEntity() {
            return Account.builder()
                    .name(this.name)
                    .email(this.email)
                    .status(this.status)
                    .build();
        }
    }

    /**
     * 회원 등록 응답 (POST /accounts)
     */
    public record CreateResponse(
            Long accountId
    ) {
      public static CreateResponse from(Account account) {
          return new CreateResponse(account.getAccountId());
      }
    }

    /**
     * 회원 정보 수정 요청 (PATCH /accounts/{id})
     */
    public record UpdateRequest(
            String name,
            String email,
            Status status,
            Boolean vipYn
    ) {
    }
}
