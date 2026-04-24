package com.marvin.boiler.global.code;

import com.marvin.boiler.global.dto.EnumResponse;
import com.marvin.boiler.global.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum 클래스를 EnumResponse 리스트로 변환하는 유틸리티
 * 다국어(i18n) 처리를 위해 MessageUtils를 활용합니다.
 */
@Component
@RequiredArgsConstructor
public class EnumMapper {

    private final MessageUtils messageUtils;

    /**
     * Enum 클래스를 받아 다국어가 적용된 EnumResponse 리스트로 변환합니다.
     */
    public <T extends BaseEnum> List<EnumResponse> toResponseList(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(e -> EnumResponse.of(e, messageUtils.getMessage(e.getDescription())))
                .collect(Collectors.toList());
    }
}
