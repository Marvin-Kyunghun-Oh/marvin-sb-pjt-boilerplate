package com.marvin.boiler.domain.account.controller;

import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.service.AccountService;
import com.marvin.boiler.global.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account", description = "계정 관리 API")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    /**
     * 회원 가입
     */
    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
    @PostMapping
    public BaseResponse<AccountApiDto.CreateResponse> createAccount(@Valid @RequestBody AccountApiDto.CreateRequest request) {
        return BaseResponse.created(accountService.createAccount(request), "common.save.success");
    }

    /**
     * 회원정보 수정
     */
    @Operation(summary = "회원 정보 수정", description = "기존 회원의 정보를 수정합니다. (Partial Update 지원)")
    @PatchMapping("/{accountId}")
    public BaseResponse<Void> updateAccount(
            @Parameter(description = "계정 ID", example = "1") @PathVariable @Min(1L) Long accountId,
            @Valid @RequestBody AccountApiDto.UpdateRequest request) {
        accountService.updateAccount(accountId, request);
        return BaseResponse.ok(null, "common.save.success");
    }

    /**
     * 회원 목록 조회
     */
    @Operation(summary = "회원 목록 조회", description = "회원 목록을 페이징하여 조회합니다.")
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            @Parameter(name = "size", description = "한 페이지당 항목 수", example = "10"),
            @Parameter(name = "sort", description = "정렬 기준 (필드명,ASC|DESC)", example = "accountId,DESC")
    })
    @GetMapping
    public BaseResponse<AccountApiDto.ListResponse> getAccounts(
            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "accountId", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return BaseResponse.ok(accountService.getAccounts(pageable));
    }

    /**
     * 회원 상세 조회
     */
    @Operation(summary = "회원 상세 조회", description = "특정 ID를 가진 회원의 상세 정보를 조회합니다.")
    @GetMapping("/{accountId}")
    public BaseResponse<AccountApiDto.GetResponse> getAccount(
            @Parameter(description = "계정 ID", example = "1") @PathVariable @Min(1L) Long accountId) {
        return BaseResponse.ok(accountService.getAccount(accountId));
    }

    @Operation(summary = "비밀번호 변경", description = "특정 ID를 가진 회원의 비밀번호를 변경합니다.")
    @PatchMapping("/{accountId}/password")
    public BaseResponse<Void> changePassword(
            @Parameter(description = "계정 ID", example = "1") @PathVariable @Min(1L) Long accountId,
            @Valid @RequestBody AccountApiDto.ChangePasswordRequest request) {

        accountService.changePassword(accountId, request);
        return BaseResponse.ok(null, "common.save.success");
    }


}
