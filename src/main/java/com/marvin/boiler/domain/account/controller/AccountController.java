package com.marvin.boiler.domain.account.controller;

import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    /**
     * 회원 가입
     * @param request
     */
    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountApiDto.CreateRequest request) {
        AccountApiDto.CreateResponse response = null;
        try {
            response = accountService.createAccount(request);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 회원정보 수정
     * @param accountId
     * @param request
     * @return
     */
    @PatchMapping("/{accountId}")
    public ResponseEntity<?> updateAccount(@Valid @PathVariable @Min(1L) Long accountId,
                                           @Valid @RequestBody AccountApiDto.UpdateRequest request) {
        try {
            accountService.updateAccount(accountId, request);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
        return ResponseEntity.ok("정상적으로 수정되었습니다.");
    }

    /**
     * 회원 목록 조회
     * @return
     */
    @GetMapping
    public ResponseEntity<AccountApiDto.ListResponse> getAccounts(
            @org.springframework.data.web.PageableDefault(page = 0, size = 10, sort = "accountId", direction = org.springframework.data.domain.Sort.Direction.DESC)
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(accountService.getAccounts(pageable));
    }

    /**
     * 회원 상세 조회
     * @param accountId
     * @return
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@Valid @PathVariable @Min(1L) Long accountId) {

        AccountApiDto.GetResponse account = null;

        try {
            account = accountService.getAccount(accountId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

        return ResponseEntity.ok(account);
    }

}
