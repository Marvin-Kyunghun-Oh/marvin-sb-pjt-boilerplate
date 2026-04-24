package com.marvin.boiler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * 국제화(i18n) 관련 설정
 */
@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        // Accept-Language 헤더를 기반으로 언어를 결정합니다. (실무 표준)
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        
        // 언어 정보가 없을 경우 기본값으로 한국어(KO)를 설정합니다.
        localeResolver.setDefaultLocale(Locale.KOREA);
        
        return localeResolver;
    }
}
