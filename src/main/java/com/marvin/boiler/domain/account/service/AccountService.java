package com.marvin.boiler.domain.account.service;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * 회원 가입
     * @param request
     */
    @Transactional
    public AccountApiDto.CreateResponse createAccount(AccountApiDto.CreateRequest request) throws RuntimeException {
        Account createAccount = accountRepository.save(request.toEntity());
        return AccountApiDto.CreateResponse.from(createAccount);
    }

    /**
     * 회원정보 수정
     * @param accountId
     * @param request
     * @throws RuntimeException
     */
    @Transactional
    public void updateAccount(Long accountId, AccountApiDto.UpdateRequest request) throws RuntimeException {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // 회원 정보 변경
        account.updateProfile(request.name(), request.email());

        // 상태 변경
        if (Objects.nonNull(request.status()))
            account.changeStatus(request.status());

        // vip 상태 변경
        if (Objects.nonNull(request.vipYn()))
            account.updateVipStatus(request.vipYn());
    }

    /**
     * 회원 목록 조회
     * @return
     */
    @Transactional(readOnly = true)
    public AccountApiDto.ListResponse getAccounts() throws RuntimeException {
        return AccountApiDto.ListResponse
                .from(accountRepository.findAll());
    }

    /**
     * 회원 상세 조회
     * @param accountId
     * @return
     */
    @Transactional(readOnly = true)
    public AccountApiDto.GetResponse getAccount(Long accountId) throws RuntimeException {
        return AccountApiDto.GetResponse
                .from(accountRepository.findById(accountId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다.")));
    }

}
