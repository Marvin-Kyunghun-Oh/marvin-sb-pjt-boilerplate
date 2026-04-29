package com.marvin.boiler.domain.account.service;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.mapper.AccountMapper;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;


    /**
     * 회원 가입
     * @param request
     */
    @Transactional
    public AccountApiDto.CreateResponse createAccount(AccountApiDto.CreateRequest request) {
        log.debug("==================== createAccount Start!!");
        Account createAccount = accountRepository.save(accountMapper.toEntity(request));
        return accountMapper.toCreateResponse(createAccount);
    }

    /**
     * 회원정보 수정
     * @param accountId
     * @param request
     * @throws RuntimeException
     */
    @Transactional
    public void updateAccount(Long accountId, AccountApiDto.UpdateRequest request) {
        log.debug("==================== updateAccount Start!!");
        Account account = this.findAccountById(accountId);

        // 회원 정보 변경
        account.updateName(request.name());
        account.updateEmail(request.email());
        // 상태 변경
        account.changeStatus(request.status());
        // vip 상태 변경
        account.updateVipStatus(request.vipYn());
    }

    /**
     * 회원 목록 조회
     * @return
     */
    @Transactional(readOnly = true)
    public AccountApiDto.ListResponse getAccounts(Pageable pageable) {
        log.debug("==================== getAccounts Start!!");
        return accountMapper.toListResponse(accountRepository.findAll(pageable));
    }

    /**
     * 회원 상세 조회
     * @param accountId
     * @return
     */
    @Transactional(readOnly = true)
    public AccountApiDto.GetResponse getAccount(Long accountId) {
        log.debug("==================== getAccount Start!!");
        return accountMapper.toDetailResponse(this.findAccountById(accountId));
    }

    /**
     * 비밀번호 변경
     * @param accountId
     * @param request
     */
    @Transactional
    public void changePassword(Long accountId, AccountApiDto.ChangePasswordRequest request) {
        log.debug("==================== changePassword Start!! accountId: {}", accountId);

        // 유효성 검증

        // 회원 비밀번호 변경
        Account account = this.findAccountById(accountId);
        account.changePassword(request.newPassword());
    }

    @Transactional(readOnly = true)
    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BizException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

}
