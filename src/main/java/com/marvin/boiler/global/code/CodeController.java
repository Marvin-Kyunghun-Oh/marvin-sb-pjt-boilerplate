package com.marvin.boiler.global.code;

import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.global.dto.BaseResponse;
import com.marvin.boiler.global.dto.EnumResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 시스템 공통 코드(Enum) 조회 컨트롤러
 */
@Tag(name = "Common-Code", description = "공통 코드 관리 API")
@RestController
@RequestMapping("/common/codes")
@RequiredArgsConstructor
public class CodeController {

    /**
     * 특정 도메인별 코드 조회가 필요한 경우
     */
    @Operation(summary = "회원 상태 코드 조회", description = "회원 도메인에서 사용하는 상태 코드 목록을 조회합니다.")
    @GetMapping("/account-status")
    public BaseResponse<List<EnumResponse>> getAccountStatus() {
        return BaseResponse.ok(EnumMapper.toResponseList(Status.class));
    }

    /**
     * 모든 공통 코드를 한 번에 조회 (프론트엔드 초기 로딩 시 유용)
     */
    @Operation(summary = "전체 공통 코드 조회", description = "시스템에서 사용하는 모든 공통 코드(Enum) 목록을 한 번에 조회합니다.")
    @GetMapping
    public BaseResponse<Map<String, List<EnumResponse>>> getAllCodes() {
        Map<String, List<EnumResponse>> codes = new LinkedHashMap<>();
        
        // 회원 상태 코드
        codes.put("accountStatus", EnumMapper.toResponseList(Status.class));
        
        // 새로운 Enum 추가 시 여기에 put()만 추가하면 됨
        // codes.put("orderType", EnumMapper.toResponseList(OrderType.class));

        return BaseResponse.ok(codes);
    }
}
