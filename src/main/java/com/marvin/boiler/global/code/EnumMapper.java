package com.marvin.boiler.global.code;

import com.marvin.boiler.global.dto.EnumResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum 클래스를 EnumResponse 리스트로 변환하는 유틸리티
 */
public class EnumMapper {
    public static <T extends BaseEnum> List<EnumResponse> toResponseList(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(EnumResponse::of)
                .collect(Collectors.toList());
    }
}
