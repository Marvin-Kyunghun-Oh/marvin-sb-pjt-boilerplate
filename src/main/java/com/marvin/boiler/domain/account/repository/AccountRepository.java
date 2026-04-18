package com.marvin.boiler.domain.account.repository;

import com.marvin.boiler.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}