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
public class AccountServiceImpl implements  AccountService {

        private final AccountRepository accoutRepository;
        private final Clock clock;

        @Override
        public Account deposit(@NonNull String clientId, @NonNull UUID accountId, @NonNull BigDecimal amount) {
                Account loadedAccount = checkAndGetAccount(clientId, accountId);
                Account newAccount = loadedAccount.deposit(amount, clock);
                accoutRepository.save(newAccount);
                return newAccount;
        }

        @Override
        public Account withdraw(@NonNull String clientId, @NonNull UUID accountId, @NonNull BigDecimal amount) {
                Account loadedAccount = checkAndGetAccount(clientId, accountId);
                Account newAccount = loadedAccount.withdraw(amount, clock);
                accoutRepository.save(newAccount);
                return newAccount;
        }

        @Override
        public AccountHistoryResponse getHistory(String clientId, UUID accountId) {
                Account loadedAccount = checkAndGetAccount(clientId, accountId);
                List<Operation> sortedHistory = loadedAccount.getHistory()
                        .stream()
                        .sorted(Comparator.comparing(Operation::getCreationDate).reversed())
                        .collect(Collectors.toList());
                return AccountHistoryResponse.builder()
                        .balance(loadedAccount.getBalance())
                        .history(sortedHistory)
                        .build();
        }

        private Account checkAndGetAccount(String clientId, UUID accountId) {
                return accoutRepository.findByAccountIdAndClientId(accountId, clientId)
                        .orElseThrow(() -> new AccountNotFoundException("Account {} not found " + accountId));
        }
}
