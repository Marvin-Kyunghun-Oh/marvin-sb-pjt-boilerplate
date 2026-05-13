package com.marvin.boiler.global.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuthFixture {

    public static final String DEFAULT_EMAIL = "admin@marvin.com";
    public static final String DEFAULT_ROLE = "ROLE_USER";

    public static Authentication createAuthentication() {
        return createAuthentication(DEFAULT_EMAIL, DEFAULT_ROLE);
    }

    public static Authentication createAuthentication(String email, String... roles) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(roles.length > 0 ? roles[0] : DEFAULT_ROLE));
        if (roles.length > 1) {
            authorities = List.of(roles).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        
        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }
}
