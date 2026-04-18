package com.marvin.boiler.domain.account.controller;

import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    /**
     * 회원 목록 조회
     * @return
     */
    @GetMapping
    public AccountApiDto.ListResponse getAccounts() {
        return accountService.getAccounts();
    }

}
