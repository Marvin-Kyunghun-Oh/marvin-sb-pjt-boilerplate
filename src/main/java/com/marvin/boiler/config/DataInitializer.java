package com.marvin.boiler.config;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.Password;
import com.marvin.boiler.domain.account.code.Status;
import com.marvin.boiler.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;


@RequiredArgsConstructor
@Profile("local")
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. 기존 데이터가 없을 때만 실행 (중복 방지)
        if (accountRepository.count() > 0) {
            return;
        }

        log.info("초기 데이터 생성을 시작합니다...");

        // 2. 관리자 계정 생성
        Account admin = Account.builder()
                .email("admin@marvin.com")
                .name("관리자")
                .password(Password.fromRaw("Admin123!@#", passwordEncoder))
                .status(Status.ACTIVE)
                .vipYn(true)
                .build();
        accountRepository.save(admin);

        // 3. 기존 data.sql에 있던 테스트 데이터 생성
        List<Account> testAccounts = List.of(
                createAccount("USER1", "user1@gmail.com", Status.ACTIVE, false),
                createAccount("USER2", "user2@gmail.com", Status.DELETED, false),
                createAccount("USER3", "user3@gmail.com", Status.ACTIVE, true),
                createAccount("USER4", "user4@gmail.com", Status.ACTIVE, false),
                createAccount("USER5", "user5@gmail.com", Status.ACTIVE, false),
                createAccount("USER6", "user6@gmail.com", Status.SUSPENDED, false),
                createAccount("USER7", "user7@gmail.com", Status.ACTIVE, false),
                createAccount("USER8", "user8@gmail.com", Status.ACTIVE, true),
                createAccount("USER9", "user9@gmail.com", Status.ACTIVE, false)
        );

        accountRepository.saveAll(testAccounts);

        log.info("초기 데이터 생성 완료! (Admin 포함 총 {}개의 계정이 생성되었습니다.)", accountRepository.count());
    }

    private Account createAccount(String name, String email, Status status, boolean vipYn) {
        return Account.builder()
                .name(name)
                .email(email)
                .status(status)
                .password(Password.fromRaw("Password123!", passwordEncoder))
                .vipYn(vipYn)
                .build();
    }
}
