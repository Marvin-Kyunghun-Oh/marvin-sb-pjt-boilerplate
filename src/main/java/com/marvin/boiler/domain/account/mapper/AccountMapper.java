package com.marvin.boiler.domain.account.mapper;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.global.dto.PageResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING, // 스프링 빈으로 등록
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 매핑되지 않는 필드에 대해 경고 무시 (필요에 따라 WARN/ERROR)
)
public interface AccountMapper {

    /**
     * CreateAccount
     * - (POST) /accounts
     */
    @Named("toEntity")
    Account toEntity(AccountApiDto.CreateRequest request);
    @Named("toCreateResponse")
    AccountApiDto.CreateResponse toCreateResponse(Account account);

    /**
     * GetAccounts
     * - (Get) /accounts
     */
    @Named("toSummary")
    AccountApiDto.Summary toSummary(Account account);
    @IterableMapping(qualifiedByName = "toSummary")
    List<AccountApiDto.Summary> toSummaryList(List<Account> accounts);

    // List<Account> -> 최종 응답 객체 변환 (페이징관련 필드 추가 버전)
    default AccountApiDto.ListResponse toListResponse(Page<Account> page) {
        List<AccountApiDto.Summary> summaries = toSummaryList(page.getContent());
        return new AccountApiDto.ListResponse(
                PageResponse.from(page, summaries));
    }

    /**
     * GetAccount
     * - (Get) /accounts/{accountId}
     */
    @Named("toDetailResponse")
    AccountApiDto.GetResponse toDetailResponse(Account account);

}
