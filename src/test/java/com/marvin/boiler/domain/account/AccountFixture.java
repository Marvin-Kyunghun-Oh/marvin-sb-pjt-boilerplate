package com.marvin.boiler.domain.account;

import com.marvin.boiler.domain.account.code.Status;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AccountFixture {

    public static final Long ACCOUNTID_1 = 1L;
    public static final String DEFAULT_PASSWORD =  "Password123!";
    public static final String DEFAULT_EMAIL = "abc@naver.com";

    /**
     * 기본 계정 빌더 (암호화되지 않은 Password VO 포함)
     * - 도메인 로직 테스트용 (MockPasswordEncoder와 사용 권장)
     */
    public static Account.AccountBuilder createAccountBuilder() {
        return Account.builder()
                .accountId(ACCOUNTID_1)
                .email(DEFAULT_EMAIL)
                .name("테스트")
                .password(Password.of(DEFAULT_PASSWORD))
                .status(Status.ACTIVE)
                .vipYn(false);
    }

    /**
     * 암호화된 비밀번호를 가진 계정 빌더
     * - 실제 인코더를 사용하거나 Mock 인코더 결과를 반영할 때 사용
     */
    public static Account.AccountBuilder createEncodedAccountBuilder(PasswordEncoder encoder) {
        return Account.builder()
                .accountId(ACCOUNTID_1)
                .email(DEFAULT_EMAIL)
                .name("테스트")
                .password(Password.fromRaw(DEFAULT_PASSWORD, encoder))
                .status(Status.ACTIVE)
                .vipYn(false);
    }

    public static Account createAccount() {
        return createAccountBuilder().build();
    }

    public static Account createSuspendedAccount() {
        return createAccountBuilder()
                .status(Status.SUSPENDED)
                .build();
    }

    public static Account createDeletedAccount() {
        return createAccountBuilder()
                .status(Status.DELETED)
                .build();
    }

    public static Account createAccount(PasswordEncoder encoder) {
        return createEncodedAccountBuilder(encoder).build();
    }

    public static Account createVIPAccount() {
        return createAccountBuilder()
                .name("VIP유저테스트")
                .email("vip@naver.com")
                .vipYn(true)
                .build();
    }

    public static Account createVIPAccount(PasswordEncoder encoder) {
        return createEncodedAccountBuilder(encoder)
                .name("VIP유저테스트")
                .email("vip@naver.com")
                .vipYn(true)
                .build();
    }
}
