package com.arolla.banck.acount.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AccountServiceImpl implements  AccountService {

        private final AccountRepository accoutRepository;
        private final Clock clock;

        @Override
        public Account deposit(@NonNull String clientId, @NonNull UUID accountId, @NonNull BigDecimal amount) {
                Optional<Account> loadedAccount =
                        accoutRepository.findAccountByAccountIdAndClientId(accountId, clientId);
                Account newAccount = doDeposit(loadedAccount
                        .orElseThrow(() -> new AccountNotFoundException("Account {} not found " + accountId)), amount);
                accoutRepository.saveAccount(newAccount);
                return newAccount;
        }

        private Account doDeposit(Account account, BigDecimal amount) {
               return account.updateAccount(amount, clock);
        }
}
