package com.marvin.boiler.domain.account.mapper;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING, // 스프링 빈으로 등록
        unmappedTargetPolicy = ReportingPolicy.IGNORE // 매핑되지 않는 필드에 대해 경고 무시 (필요에 따라 WARN/ERROR)
)
public interface AccountMapper {

    // CreateAccount
    @Named("toEntity")
    Account toEntity(AccountApiDto.CreateRequest request);
    @Named("toCreateResponse")
    AccountApiDto.CreateResponse toCreateResponse(Account account);

    // GetAccounts
    @Named("toSummary")
    AccountApiDto.Summary toSummary(Account account);
    @IterableMapping(qualifiedByName = "toSummary")
    List<AccountApiDto.Summary> toSummaryList(List<Account> accounts);

    // List<Account> -> 최종 응답 객체 변환
    default AccountApiDto.ListResponse toListResponse(List<Account> accounts) {
        return new AccountApiDto.ListResponse(toSummaryList(accounts));
    }

    // GetAccount
    @Named("toDetailResponse")
    AccountApiDto.GetResponse toDetailResponse(Account account);

}
