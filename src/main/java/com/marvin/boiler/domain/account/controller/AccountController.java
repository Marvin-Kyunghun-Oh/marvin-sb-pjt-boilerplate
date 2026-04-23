package com.marvin.boiler.domain.account.controller;

import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.service.AccountService;
import com.marvin.boiler.global.code.EnumMapper;
import com.marvin.boiler.global.dto.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    /**
     * 회원 가입
     */
    @PostMapping
    public BaseResponse<AccountApiDto.CreateResponse> createAccount(@Valid @RequestBody AccountApiDto.CreateRequest request) {
        return BaseResponse.created(accountService.createAccount(request));
    }

    /**
     * 회원정보 수정
     */
    @PatchMapping("/{accountId}")
    public BaseResponse<Void> updateAccount(@PathVariable @Min(1L) Long accountId,
                                            @Valid @RequestBody AccountApiDto.UpdateRequest request) {
        accountService.updateAccount(accountId, request);
        return BaseResponse.noContent();
    }

    /**
     * 회원 목록 조회
     */
    @GetMapping
    public BaseResponse<AccountApiDto.ListResponse> getAccounts(
            @PageableDefault(page = 0, size = 10, sort = "accountId", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return BaseResponse.ok(accountService.getAccounts(pageable));
    }

    /**
     * 회원 상세 조회
     */
    @GetMapping("/{accountId}")
    public BaseResponse<AccountApiDto.GetResponse> getAccount(@PathVariable @Min(1L) Long accountId) {
        return BaseResponse.ok(accountService.getAccount(accountId));
    }
}
