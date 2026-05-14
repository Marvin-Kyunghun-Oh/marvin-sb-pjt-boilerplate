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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;


    /**
     * 회원 가입
     * @param request
     */
    @Transactional
    public AccountApiDto.CreateResponse createAccount(AccountApiDto.CreateRequest request) {
        log.debug("==================== createAccount Start!!");
        // 매퍼 호출 시 passwordEncoder를 @Context로 전달
        Account createAccount = accountRepository.save(accountMapper.toEntity(request, passwordEncoder));
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

        // 회원정보 조회
        Account account = this.findAccountById(accountId);

        // [유효성 검증 1] - 현재 비밀번호(oldPassword)가 불일치하는지 먼저 체크 (BCrypt matches 사용)
        if (!account.getPassword().matches(request.oldPassword(), passwordEncoder)) {
            throw new BizException(ErrorCode.ACCOUNT_INVALID_PASSWORD);
        }

        // [유효성 검증 2] - 새 비밀번호(newPassword)가 이전 패스워드와 동일한지 체크
        if (account.getPassword().matches(request.newPassword(), passwordEncoder)) {
            throw new BizException(ErrorCode.ACCOUNT_SAME_AS_OLD_PASSWORD);
        }

        // 회원 비밀번호 변경 (엔티티 내부에서 암호화 수행)
        account.changePassword(request.newPassword(), passwordEncoder);
    }


    /**
     * 회원 단건 조회
     * @param accountId
     * @return
     */
    @Transactional(readOnly = true)
    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BizException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

}
