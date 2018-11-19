package com.arolla.banck.acount.service;

import com.arolla.banck.acount.service.model.Account;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class AccountRepository implements CrudAccountRepository{

        private final List<Account> accounts;

        @Override public void save(Account newAccount) {
                Optional<Account> oldAccount = accounts
                        .stream()
                        .filter(dbAccount -> dbAccount.getId().equals(newAccount.getId()))
                        .findFirst();
                oldAccount.ifPresent(account -> accounts.remove(account));
                accounts.add(newAccount);
        }

        @Override public Optional<Account> findByAccountIdAndClientId(UUID accountId, String clientId) {
                return accounts
                        .stream()
                        .filter(account -> account.getId().equals(accountId)
                                && account.getOwner().getId().equals(clientId))
                        .findFirst();
        }
}
