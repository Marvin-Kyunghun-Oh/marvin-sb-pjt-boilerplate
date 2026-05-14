package com.marvin.boiler.domain.auth.service;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import com.marvin.boiler.domain.auth.dto.AuthApiDto;
import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import com.marvin.boiler.global.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public AuthApiDto.TokenResponse login(AuthApiDto.LoginRequest request) {

        // 보안 관행에 따라 이메일 존재 여부와 비밀번호 일치 여부를 동일한 에러(AUTH_LOGIN_FAILED)로 처리
        Account account = accountRepository.findByEmail(request.email())
                .filter(a -> a.getPassword().matches(request.password(), passwordEncoder))
                .orElseThrow(() -> new BizException(ErrorCode.AUTH_LOGIN_FAILED));

        // TODO : [유효성 검증] 회원 상태 체크

        // 토큰 발급
        Authentication authentication = getAuthenticationByAccount(account);
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        // TODO : 토큰정보 DB에 넣기

        return new AuthApiDto.TokenResponse(
                "Bearer",
                accessToken,
                refreshToken,
                tokenProvider.getAccessTokenExpirationTime()
        );
    }

    /**
     * 회원정보로 권한객체 가져오기
     */
    private Authentication getAuthenticationByAccount(Account account) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // VIP 여부에 따른 추가 권한 부여
        if (Boolean.TRUE.equals(account.getVipYn())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VIP"));
        }

        return new UsernamePasswordAuthenticationToken(account.getEmail(), null, authorities);
    }
}
