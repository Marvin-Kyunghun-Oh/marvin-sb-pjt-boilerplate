package com.marvin.boiler.domain.account.mapper;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.Password;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.global.dto.PageResponse;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {

    /**
     * CreateAccount
     * - (POST) /accounts
     */
    @Named("toEntity")
    Account toEntity(AccountApiDto.CreateRequest request, @Context PasswordEncoder passwordEncoder);
    // [toEntity] String -> Password 변환 시 @Context로 전달된 encoder 사용
    default Password toPassword(String password, @Context PasswordEncoder passwordEncoder) {
        return Password.fromRaw(password, passwordEncoder);
    }

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

    // Page<Account> -> 최종 응답 객체 변환 (PageResponse.of 활용)
    default AccountApiDto.ListResponse toListResponse(Page<Account> page) {
        return new AccountApiDto.ListResponse(
                PageResponse.of(page, this::toSummary));
    }

    /**
     * GetAccount
     * - (Get) /accounts/{accountId}
     */
    @Named("toDetailResponse")
    AccountApiDto.GetResponse toDetailResponse(Account account);
    

}
