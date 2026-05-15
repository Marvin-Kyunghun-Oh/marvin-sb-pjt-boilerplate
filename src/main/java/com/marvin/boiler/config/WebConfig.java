package com.marvin.boiler.config;

import com.marvin.boiler.global.security.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 커스텀 @CurrentUser 리졸버 등록
        resolvers.add(new CurrentUserArgumentResolver());
    }
}
