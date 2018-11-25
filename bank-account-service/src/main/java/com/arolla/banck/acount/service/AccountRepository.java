package com.arolla.banck.acount.service;

import com.arolla.banck.acount.service.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
        void save(Account account);
        Optional<Account> findByAccountIdAndClientId(UUID accountId);
}
