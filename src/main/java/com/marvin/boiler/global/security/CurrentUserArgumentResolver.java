package com.marvin.boiler.global.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @CurrentUser 어노테이션이 붙은 파라미터를 처리하는 리졸버
 * - SecurityContext에서 인증 정보를 꺼내 사용자 ID(Long)를 반환합니다.
 */
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 파라미터에 @CurrentUser 어노테이션이 있고, 타입이 Long인 경우에만 지원
        return parameter.hasParameterAnnotation(CurrentUser.class) && 
               parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            // 현재 보일러플레이트는 username 필드에 accountId를 String으로 저장하고 있음
            return Long.parseLong(userDetails.getUsername());
        }

        return null;
    }
}
