package com.marvin.boiler.global.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 다국어 메시지 조회를 위한 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class MessageUtils {

    private final MessageSource messageSource;

    /**
     * 현재 로케일에 맞는 메시지를 조회합니다.
     * @param code 메시지 키
     * @return 번역된 메시지
     */
    public String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    /**
     * 파라미터가 포함된 메시지를 조회합니다.
     * @param code 메시지 키
     * @param args 파라미터 배열
     * @return 번역된 메시지
     */
    public String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
