package com.marvin.boiler.global.test;

import com.marvin.boiler.config.LocaleConfig;
import com.marvin.boiler.config.SecurityConfig;
import com.marvin.boiler.global.dto.BaseResponseInterceptor;
import com.marvin.boiler.global.utils.MessageUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebMvcTest // 원본 애노테이션을 메타 애노테이션으로 포함
@Import({
        MessageUtils.class,
        LocaleConfig.class,
        SecurityConfig.class,
        BaseResponseInterceptor.class // 인터셉터 추가

}) // 공통으로 필요한 빈 설정
public @interface WebMvcTestConfig {

    /**
     * 기존 @WebMvcTest의 value 옵션
     * - @WebMvcTest(XXXController.class)
     */
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] value() default {};

    /**
     * 기존 @WebMvcTest의 controllers 옵션
     * @WebMvcTest(controllers = XXXController.class)
     */
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};


}
