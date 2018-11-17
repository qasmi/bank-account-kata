package com.arolla.banck.acount.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountServiceImpl implements  AccountService {

        private final AccountRepository accoutRepository;
        private final Clock clock;

        @Override
        public Account deposit(@NonNull String clientId, @NonNull UUID accountId, @NonNull BigDecimal amount) {
                Account loadedAccount = checkAndGetAccount(clientId, accountId);
                Account newAccount = loadedAccount.deposit(amount, clock);
                accoutRepository.saveAccount(newAccount);
                return newAccount;
        }

        @Override
        public Account withdraw(@NonNull String clientId, @NonNull UUID accountId, @NonNull BigDecimal amount) {
                Account loadedAccount = checkAndGetAccount(clientId, accountId);
                Account newAccount = loadedAccount.withdraw(amount, clock);
                accoutRepository.saveAccount(newAccount);
                return newAccount;
        }

        private Account checkAndGetAccount(String clientId, UUID accountId) {
                return accoutRepository.findAccountByAccountIdAndClientId(accountId, clientId)
                        .orElseThrow(() -> new AccountNotFoundException("Account {} not found " + accountId));
        }
}
