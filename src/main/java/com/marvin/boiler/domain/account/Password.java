package com.marvin.boiler.domain.account;

import com.marvin.boiler.global.exception.BizException;
import com.marvin.boiler.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public static Password of(String value) {
        validate(value);
        return new Password(value);
    }

    private static void validate(String value) {
        if (!StringUtils.hasText(value) || !PATTERN.matcher(value).matches()) {
            throw new BizException(ErrorCode.ACCOUNT_INVALID_NEWPASSWORD_PATTERN);
        }
    }

    public boolean isSame(String rawPassword) {
        return this.value.equals(rawPassword);
    }
}
