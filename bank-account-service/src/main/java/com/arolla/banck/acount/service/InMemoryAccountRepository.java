package com.arolla.banck.acount.service;

import com.arolla.banck.acount.service.model.Account;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class InMemoryAccountRepository implements AccountRepository {

        private final Map<UUID, Account> accounts;

        @Override public void save(Account newAccount) {
                accounts.put(newAccount.getId(), newAccount);
        }

        @Override public Optional<Account> findByAccountIdAndClientId(UUID accountId) {
                return accounts.entrySet()
                        .stream()
                        .filter(accountEntry -> accountEntry.getKey().equals(accountId))
                        .map(Map.Entry::getValue)
                        .findFirst();
        }
}