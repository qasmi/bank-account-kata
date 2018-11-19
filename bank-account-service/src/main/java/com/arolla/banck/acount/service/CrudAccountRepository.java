package com.arolla.banck.acount.service;

import java.util.Optional;
import java.util.UUID;

public interface CrudAccountRepository {
        void saveAccount(Account account);
        Optional<Account> findAccountByAccountIdAndClientId(UUID accountId, String clientId);
}
