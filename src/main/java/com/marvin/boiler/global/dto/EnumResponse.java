package com.marvin.boiler.global.dto;

import com.marvin.boiler.global.code.BaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum 목록을 API로 전달하기 위한 표준 DTO
 */
@Schema(description = "공통 코드 응답 규격")
public record EnumResponse(
        @Schema(description = "코드 값 (시스템 내부용)", example = "ACTIVE")
        String key,
        @Schema(description = "코드 명칭 (화면 표시용)", example = "활성")
        String value
) {
    /**
     * Enum과 번역된 설명을 바탕으로 응답 객체를 생성합니다.
     */
    public static EnumResponse of(BaseEnum baseEnum, String translatedValue) {
        return new EnumResponse(baseEnum.getName(), translatedValue);
    }
}
