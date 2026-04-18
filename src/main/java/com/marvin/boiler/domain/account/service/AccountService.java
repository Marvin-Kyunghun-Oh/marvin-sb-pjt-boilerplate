package com.marvin.boiler.domain.account.service;

import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;


    /**
     * 회원 목록 조회
     * @return
     */
    @Transactional(readOnly = true)
    public AccountApiDto.ListResponse getAccounts() {
        return AccountApiDto.ListResponse
                .from(accountRepository.findAll());
    }

}
