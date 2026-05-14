package com.marvin.boiler.config;

import com.marvin.boiler.global.security.JwtFilter;
import com.marvin.boiler.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (REST API는 Stateless하므로 보통 비활성화)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. H2 Console 사용을 위한 FrameOptions 설정 (SameOrigin 허용)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                // 3. 세션 정책 설정 (JWT 사용을 위해 STATELESS 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. API 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 및 H2 Console 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // Swagger UI 및 OpenAPI 명세 관련 경로 허용
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**"
                        ).permitAll()
                        
                        // 공개 API (가입, 로그인 등)
                        .requestMatchers("/accounts/**", "/auth/**").permitAll()
                        
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 5. JWT 필터 추가
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}
