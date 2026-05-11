package com.marvin.boiler.domain.account.repository;

import com.marvin.boiler.domain.account.Account;
import com.marvin.boiler.domain.account.AccountFixture;
import com.marvin.boiler.global.test.DataJpaTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTestConfig
@DisplayName("AccountRepository 테스트")
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String DEFAULT_NEW_PASSWORD = "NewPassword123!";

    @Nested
    @DisplayName("회원 저장 및 조회")
    class SaveAndFind {

        @Test
        @DisplayName("성공: 회원 저장 시 Auditing 기능이 작동하여 시간이 자동 입력된다")
        void save_success_with_auditing() {
            // given
            Account account = AccountFixture.createAccountBuilder()
                    .accountId(null)
                    .build();

            // when
            Account savedAccount = accountRepository.save(account);

            // then
            assertThat(savedAccount.getAccountId()).isNotNull();
            assertThat(savedAccount.getCreatedAt()).isNotNull();
            assertThat(savedAccount.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("성공: 이메일로 회원을 조회할 수 있다")
        void findByEmail_success() {
            // given
            String email = "findme@test.com";
            Account account = AccountFixture.createAccountBuilder()
                    .accountId(null)
                    .email(email)
                    .build();
            entityManager.persist(account);
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<Account> foundAccount = accountRepository.findByEmail(email);

            // then
            assertThat(foundAccount).isPresent();
            assertThat(foundAccount.get().getEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("실패: 중복된 이메일 저장 시 예외가 발생한다")
        void save_fail_duplicate_email() {
            // given
            String email = "duplicate@test.com";
            Account account1 = AccountFixture.createAccountBuilder().accountId(null).email(email).build();
            accountRepository.saveAndFlush(account1);

            Account account2 = AccountFixture.createAccountBuilder().accountId(null).email(email).build();

            // when & then
            assertThatThrownBy(() -> accountRepository.saveAndFlush(account2))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("회원 수정")
    class Update {

        @Test
        @DisplayName("성공: 비밀번호 변경 시 updatedAt이 갱신된다")
        void update_password_success_with_updatedAt_change() {
            // given
            Account account = AccountFixture.createAccountBuilder()
                    .accountId(null)
                    .build();
            Account savedAccount = accountRepository.saveAndFlush(account);
            LocalDateTime initialUpdatedAt = savedAccount.getUpdatedAt();

            // when
            savedAccount.changePassword(DEFAULT_NEW_PASSWORD, passwordEncoder);
            accountRepository.saveAndFlush(savedAccount);
            entityManager.clear();

            // then
            Account updatedAccount = accountRepository.findById(savedAccount.getAccountId()).orElseThrow();
            assertThat(updatedAccount.getUpdatedAt()).isNotEqualTo(initialUpdatedAt);
            assertThat(passwordEncoder.matches(DEFAULT_NEW_PASSWORD, updatedAccount.getPassword().getValue())).isTrue();
        }
    }
}
