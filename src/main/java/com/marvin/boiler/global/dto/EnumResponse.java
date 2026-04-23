package com.marvin.boiler.global.dto;

import com.marvin.boiler.global.code.BaseEnum;

/**
 * Enum 목록을 API로 전달하기 위한 표준 DTO
 */
public record EnumResponse(
        String key,
        String value
) {
    public static EnumResponse of(BaseEnum baseEnum) {
        return new EnumResponse(baseEnum.getName(), baseEnum.getDescription());
    }
}
