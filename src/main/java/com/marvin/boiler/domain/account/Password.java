package com.marvin.boiler.domain.account;

import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    public static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @Column(name = "password", nullable = false)
    private String value;

    private Password(String value) {
        this.value = value;
    }

    /**
     * 생짜 비밀번호로부터 Password 객체 생성 (검증 후 암호화)
     */
    public static Password fromRaw(String rawPassword, PasswordEncoder encoder) {
        validate(rawPassword);
        return new Password(encoder.encode(rawPassword));
    }

    /**
     * 이미 암호화된 비밀번호로부터 Password 객체 생성 (DB 조회용)
     */
    public static Password fromEncoded(String encodedPassword) {
        return new Password(encodedPassword);
    }

    public static Password of(String value) {
        validate(value);
        return new Password(value);
    }

    private static void validate(String value) {
        if (!StringUtils.hasText(value) || !PATTERN.matcher(value).matches()) {
            throw new BizException(ErrorCode.ACCOUNT_INVALID_NEWPASSWORD_PATTERN);
        }
    }

    /**
     * 비밀번호 일치 여부 확인 (BCrypt matches 사용)
     */
    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.value);
    }

    /**
     * @deprecated matches(String, PasswordEncoder)를 사용하세요.
     */
    @Deprecated
    public boolean isSame(String rawPassword) {
        return this.value.equals(rawPassword);
    }
}
