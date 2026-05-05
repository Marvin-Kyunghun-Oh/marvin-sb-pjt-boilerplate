package com.marvin.boiler.domain.account;

import com.marvin.boiler.domain.account.code.Status;

public class AccountFixture {

    public static final Long ACCOUNTID_1 = 1L;
    public static final String DEFAULT_PASSWORD = "Password123!";
    public static final String DEFAULT_EMAIL = "abc@naver.com";

    // 기본형
    public static Account.AccountBuilder createAccountBuilder() {
        return Account.builder()
                .accountId(ACCOUNTID_1)
                .email(DEFAULT_EMAIL)
                .name("테스트")
                .password(Password.of(DEFAULT_PASSWORD))
                .status(Status.ACTIVE)
                .vipYn(false);
    }


    // 일반 객체 생성
    public static Account createAccount() {
        return createAccountBuilder().build();
    }

    // VIP 객체 생성
    public static Account createVIPAccount() {
        return createAccountBuilder()
                .name("VIP유저테스트")
                .email("vip@naver.com")
                .vipYn(true)
                .build();
    }

}
