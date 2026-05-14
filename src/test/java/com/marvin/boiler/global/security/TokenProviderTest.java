package com.marvin.boiler.global.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenProviderTest {

    private TokenProvider tokenProvider;

    // 테스트용 설정값
    private final String secret = "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK";
    private final long accessTokenExpiration = 1800000; // 30분
    private final long refreshTokenExpiration = 604800000; // 7일

    @BeforeEach
    void setUp() {
        // 실제 Spring 빈 주입이 아닌, 순수 자바 객체로 단위 테스트 진행
        tokenProvider = new TokenProvider(secret, accessTokenExpiration, refreshTokenExpiration);
        tokenProvider.init(); // @PostConstruct 수동 호출
    }

    @Test
    @DisplayName("성공: 유효한 인증 정보를 주면 엑세스 토큰을 생성한다.")
    void createAccessToken_Success() {
        // given
        Authentication authentication = TokenFixture.createAuthentication();

        // when
        String token = tokenProvider.createAccessToken(authentication);

        // then
        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("성공: 생성된 토큰에서 인증 정보(Authentication)를 올바르게 추출한다.")
    void getAuthentication_Success() {
        // given
        String email = "test@marvin.com";
        Authentication auth = TokenFixture.createAuthentication(email, "ROLE_USER");
        String token = tokenProvider.createAccessToken(auth);

        // when
        Authentication resultAuth = tokenProvider.getAuthentication(token);

        // then
        assertThat(resultAuth.getName()).isEqualTo(email);
        assertThat(resultAuth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("실패: 잘못된 서명(Secret Key)으로 서명된 토큰은 유효성 검증에 실패한다.")
    void validateToken_InvalidSignature() {
        // given
        SecretKey wrongKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("d3Jvbmctc2VjcmV0LWtleS1mb3ItdGVzdGluZy1wdXJwb3Nlcy1vbmx5LW5vdC1mb3ItcHJvZA=="));
        String invalidToken = Jwts.builder()
                .subject("user")
                .signWith(wrongKey)
                .compact();

        // when & then
        assertThrows(SignatureException.class, () -> {
            tokenProvider.validateToken(invalidToken);
        });
    }

    @Test
    @DisplayName("실패: 만료된 토큰은 유효성 검증에 실패한다.")
    void validateToken_Expired() {
        // given
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        String expiredToken = Jwts.builder()
                .subject("expired-user")
                .expiration(new Date(System.currentTimeMillis() - 1000)) // 1초 전 만료
                .signWith(key)
                .compact();

        // when & then
        assertThrows(ExpiredJwtException.class, () -> {
            tokenProvider.validateToken(expiredToken);
        });
    }
}
