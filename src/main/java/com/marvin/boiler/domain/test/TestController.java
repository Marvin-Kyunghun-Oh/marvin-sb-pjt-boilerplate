package com.marvin.boiler.domain.test;

import com.marvin.boiler.domain.account.dto.AccountApiDto;
import com.marvin.boiler.domain.account.service.AccountService;
import com.marvin.boiler.global.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "테스트 API")
@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final AccountService accountService;

    /**
     * 회원 상세 조회 (테스트)
     *  - 권한 인증 테스트 용
     */
    @Operation(summary = "회원 상세 조회(테스트)", description = "특정 ID를 가진 회원의 상세 정보를 조회합니다.")
    @GetMapping("/{accountId}")
    public BaseResponse<AccountApiDto.GetResponse> getAccount(
            @Parameter(description = "계정 ID", example = "1") @PathVariable @Min(1L) Long accountId) {
        return BaseResponse.ok(accountService.getAccount(accountId));
    }

}
