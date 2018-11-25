package com.arolla.banck.acount.service;

import com.arolla.banck.acount.service.exception.AccountNotFoundException;
import com.arolla.banck.acount.service.model.Account;
import com.arolla.banck.acount.service.model.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AccountManager implements AccountApi {

        private final InMemoryAccountRepository accountRepository;
        private final Clock clock;

        @Override
        public Account deposit(@NonNull UUID accountId, @NonNull BigDecimal amount) {
                Account loadedAccount = checkAndGetAccount(accountId);
                loadedAccount.deposit(amount, clock);
                accountRepository.save(loadedAccount);
                return loadedAccount;
        }

        @Override
        public Account withdraw(@NonNull UUID accountId, @NonNull BigDecimal amount) {
                Account loadedAccount = checkAndGetAccount(accountId);
                loadedAccount.withdraw(amount, clock);
                accountRepository.save(loadedAccount);
                return loadedAccount;
        }

        @Override
        public AccountHistoryResponse getHistory(UUID accountId) {
                Account loadedAccount = checkAndGetAccount(accountId);
                List<Operation> sortedHistory = loadedAccount.getHistory()
                        .stream()
                        .sorted(Comparator.comparing(Operation::getCreationDate).reversed())
                        .collect(Collectors.toList());
                return AccountHistoryResponse.builder()
                        .balance(loadedAccount.getBalance())
                        .history(sortedHistory)
                        .build();
        }

        private Account checkAndGetAccount(UUID accountId) {
                return accountRepository.findByAccountIdAndClientId(accountId)
                        .orElseThrow(() -> new AccountNotFoundException(
                                String.format("Account %s not found ", accountId)));
        }
}