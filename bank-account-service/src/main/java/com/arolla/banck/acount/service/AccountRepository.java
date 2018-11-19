package com.arolla.banck.acount.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.*;

public class AccountRepository implements CrudAccountRepository{

        private static List<Account> accounts;
        private final Clock clock;

        public AccountRepository(final Clock clock){
                this.clock = clock;
                Client client1 = new Client("93405676", "arolla");
                Account account1 = Account.builder()
                        .id(UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56459"))
                        .owner(client1)
                        .creationDate(clock.instant())
                        .solde(Solde.builder().amount(new BigDecimal(300))
                                .build())
                        .build();
                accounts = new ArrayList<>();
                accounts.add(account1);
        }

        @Override public void saveAccount(Account newAccount) {
                Optional<Account> oldAccount = accounts
                        .stream()
                        .filter(dbAccount -> dbAccount.getId().equals(newAccount.getId()))
                        .findFirst();
                oldAccount.ifPresent(account -> accounts.remove(account));
                accounts.add(newAccount);
        }

        @Override public Optional<Account> findAccountByAccountIdAndClientId(UUID accountId, String clientId) {
                return accounts
                        .stream()
                        .filter(account -> account.getId().equals(accountId)
                                && account.getOwner().getId().equals(clientId))
                        .findFirst();
        }
}
